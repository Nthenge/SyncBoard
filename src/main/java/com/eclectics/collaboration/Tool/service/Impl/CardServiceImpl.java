package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.AssignedCardResponseDTO;
import com.eclectics.collaboration.Tool.dto.CardMoveRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import com.eclectics.collaboration.Tool.enums.BoardRole;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.CardMapper;
import com.eclectics.collaboration.Tool.mapper.LabelMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.*;
import com.eclectics.collaboration.Tool.service.CardService;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ListEntityRepository listRepository;
    private final UserRespository userRepository;
    private final CardMapper cardMapper;
    private final BoardMemberRepository boardMemberRepository;
    private final CardAssigneeRepository cardAssigneeRepository;
    private final CardLabelRepository cardLabelRepository;
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final UserRecentBoardRepository recentBoardRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    // ─── CREATE ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CardResponseDTO createCard(Long listId, CardRequestDTO dto, Long userId) {

        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("List not found"));

        Boards board = list.getBoard();

        boardMemberRepository
                .findByBoardIdAndUserId(board.getId(), userId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Card card = cardMapper.toEntity(dto, list, user);

        if (card.getPosition() == null) {
            Integer maxPos = cardRepository.findByListIdOrderByPosition(listId).stream()
                    .map(Card::getPosition)
                    .max(Integer::compareTo)
                    .orElse(0);
            card.setPosition(maxPos + 1);
        }

        card.setUpdatedAt(LocalDateTime.now());
        Card savedCard = cardRepository.save(card);

        cardAssigneeRepository.save(new CardAssignee(savedCard, user));

        trackActivity(user, board, list, savedCard);

        return enrich(savedCard);
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Override
    public List<CardResponseDTO> getCardsByList(Long listId) {
        return cardRepository.findByListIdOrderByPosition(listId)
                .stream()
                .map(this::enrich)
                .toList();
    }

    @Override
    public CardResponseDTO getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Card not found"));
        return enrich(card);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public CardResponseDTO updateCard(Long cardId, Long userId, CardRequestDTO dto) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        Boards board = card.getList().getBoard();

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(board.getId(), userId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (!card.getCreatedBy().getId().equals(userId) && member.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Cannot update this card");
        }

        cardMapper.updateEntityFromDto(dto, card);

        if (Boolean.TRUE.equals(dto.getClearDueDate())) {
            card.setDueDate(null);
        }

        card.setUpdatedAt(LocalDateTime.now());
        Card savedCard = cardRepository.save(card);

        trackActivity(member.getUser(), board, card.getList(), savedCard);

        return enrich(savedCard);
    }

    // ─── MOVE ─────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public CardResponseDTO moveCard(Long cardId, CardMoveRequestDTO dto, Long userId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        // Validate user is a member of the source board
        Boards sourceBoard = card.getList().getBoard();
        BoardMember requester = boardMemberRepository
                .findByBoardIdAndUserId(sourceBoard.getId(), userId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("Only members of a board can move cards."));

        // Only the card's assignee(s) or a board admin may move the card
        boolean isAssignee = cardAssigneeRepository.existsByCardIdAndUserId(cardId, userId);
        if (!requester.isAdmin() && !isAssignee) {
            throw new CollaborationExceptions.ForbiddenException(
                    "Only the assigned member or a board admin can move this card.");
        }

        ListEntity targetList = listRepository.findById(dto.getTargetListId())
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Target list not found"));

        // If moving across boards, verify membership + permission on target board too
        Boards targetBoard = targetList.getBoard();
        if (!targetBoard.getId().equals(sourceBoard.getId())) {
            BoardMember targetRequester = boardMemberRepository
                    .findByBoardIdAndUserId(targetBoard.getId(), userId)
                    .orElseThrow(() ->
                            new CollaborationExceptions.ForbiddenException("User is not a member of the target board"));

            if (!targetRequester.isAdmin() && !isAssignee) {
                throw new CollaborationExceptions.ForbiddenException(
                        "Only the assigned member or a board admin can move this card to another board.");
            }
        }

        // Shift positions in the target list to make room at newIndex
        List<Card> targetCards = cardRepository.findByListIdOrderByPosition(dto.getTargetListId());

        // Remove the card from source list and compact positions
        List<Card> sourceCards = cardRepository.findByListIdOrderByPosition(card.getList().getId());
        sourceCards.remove(card);
        for (int i = 0; i < sourceCards.size(); i++) {
            sourceCards.get(i).setPosition(i + 1);
        }
        cardRepository.saveAll(sourceCards);

        // Insert card at newIndex in target list
        int insertAt = (dto.getNewIndex() != null)
                ? Math.min(dto.getNewIndex(), targetCards.size())
                : targetCards.size();

        targetCards.add(insertAt, card);
        for (int i = 0; i < targetCards.size(); i++) {
            targetCards.get(i).setPosition(i + 1);
        }
        cardRepository.saveAll(targetCards);

        // Update the card's list reference
        card.setList(targetList);
        card.setPosition(insertAt + 1);
        card.setUpdatedAt(LocalDateTime.now());

        Card savedCard = cardRepository.save(card);

        trackActivity(requester.getUser(), targetBoard, targetList, savedCard);

        return enrich(savedCard);
    }


    @Transactional
    @Override
    public void deleteCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        Boards board = card.getList().getBoard();

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(board.getId(), userId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (!card.getCreatedBy().getId().equals(userId) && member.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Cannot delete this card");
        }

        cardAssigneeRepository.deleteByCardId(card.getId());
        cardRepository.delete(card);
    }

    @Override
    @Transactional
    public CardResponseDTO reassignCard(Long cardId, Long newAssigneeUserId, Long requestingUserId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        Long boardId = card.getList().getBoard().getId();

        BoardMember requester = boardMemberRepository.findByBoardIdAndUserId(boardId, requestingUserId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (!requester.isAdmin()) {
            throw new CollaborationExceptions.ForbiddenException("Only board admins can assign cards.");
        }

        boardMemberRepository.findByBoardIdAndUserId(boardId, newAssigneeUserId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("New assignee is not a member of the board"));

        User newAssignee = userRepository.findById(newAssigneeUserId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        List<CardAssignee> existingAssignees = cardAssigneeRepository.findByCardId(cardId);
        boolean alreadyAssigned = existingAssignees.stream()
                .anyMatch(a -> a.getUser().getId().equals(newAssigneeUserId));

        existingAssignees.forEach(existing ->
                cardAssigneeRepository.deleteByCardIdAndUserId(cardId, existing.getUser().getId()));

        cardAssigneeRepository.save(new CardAssignee(card, newAssignee));

        card.setUpdatedAt(LocalDateTime.now());
        Card savedCard = cardRepository.save(card);

        trackActivity(requester.getUser(), card.getList().getBoard(), card.getList(), savedCard);

        if (!alreadyAssigned) {
            notifyAssignment(requester.getUser(), newAssignee, savedCard);
        }

        return enrich(savedCard);
    }

    private void notifyAssignment(User assigner, User newAssignee, Card card) {
        try {
            NotificationPreference pref = notificationService.getOrCreatePreferenceEntity(newAssignee.getId());

            if (pref.isInAppOnAssign()) {
                notificationService.create(
                        newAssignee,
                        Notification.Type.CARD_ASSIGNED,
                        String.format("%s assigned you to \"%s\"", assigner.getFullName(), card.getTitle()),
                        "CARD",
                        card.getId()
                );
            }

            if (pref.isEmailOnAssign()) {
                emailService.sendCardAssignedNotification(newAssignee.getEmail(), assigner.getFullName(), card.getTitle());
            }
        } catch (Exception e) {
            log.error("Failed to send card assignment notification to user {} for card {}: {}",
                    newAssignee.getId(), card.getId(), e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void attachLabel(Long cardId, Long labelId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        boardMemberRepository.findByBoardIdAndUserId(card.getList().getBoard().getId(), userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (cardLabelRepository.existsByCardIdAndLabelId(cardId, labelId)) return;

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Label not found"));

        cardLabelRepository.save(new CardLabel(card, label));
    }

    @Override
    @Transactional
    public void detachLabel(Long cardId, Long labelId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        boardMemberRepository.findByBoardIdAndUserId(card.getList().getBoard().getId(), userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        cardLabelRepository.deleteByCardIdAndLabelId(cardId, labelId);
    }

    private void trackActivity(User user, Boards board, ListEntity list, Card card) {
        UserRecentBoard entry = recentBoardRepository
                .findByUserBoardListCard(user.getId(), board.getId(), list.getId(), card.getId())
                .orElseGet(() -> new UserRecentBoard(user, board, list, card));

        entry.setLastAccessedAt(LocalDateTime.now());
        recentBoardRepository.save(entry);
    }

    private CardResponseDTO enrich(Card card) {
        CardResponseDTO dto = cardMapper.toDto(card);

        cardAssigneeRepository.findByCardId(card.getId()).stream().findFirst().ifPresent(ca -> {
            dto.setAssigneeId(ca.getUser().getId());
            dto.setAssigneeName(ca.getUser().getFullName());
        });

        dto.setLabels(
                cardLabelRepository.findByCardId(card.getId()).stream()
                        .map(cl -> labelMapper.toDto(cl.getLabel()))
                        .toList()
        );

        return dto;
    }

    @Override
    public List<AssignedCardResponseDTO> getAssignedCards(Long userId) {
        return cardAssigneeRepository.findAssignedCardsByUserId(userId);
    }
}
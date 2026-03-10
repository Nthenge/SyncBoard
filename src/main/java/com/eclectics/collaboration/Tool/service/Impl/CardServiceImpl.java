package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.CardMoveRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.CardMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.*;
import com.eclectics.collaboration.Tool.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ListEntityRepository listRepository;
    private final UserRespository userRepository;
    private final CardMapper cardMapper;
    private final BoardMemberRepository boardMemberRepository;
    private final CardAssigneeRepository cardAssigneeRepository;

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

        Card savedCard = cardRepository.save(card);

        cardAssigneeRepository.save(new CardAssignee(savedCard, user));

        return cardMapper.toDto(savedCard);
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Override
    public List<CardResponseDTO> getCardsByList(Long listId) {
        return cardRepository.findByListIdOrderByPosition(listId)
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Override
    public CardResponseDTO getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Card not found"));
        return cardMapper.toDto(card);
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
        return cardMapper.toDto(cardRepository.save(card));
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
        boardMemberRepository
                .findByBoardIdAndUserId(sourceBoard.getId(), userId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        ListEntity targetList = listRepository.findById(dto.getTargetListId())
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Target list not found"));

        // If moving across boards, verify membership on target board too
        Boards targetBoard = targetList.getBoard();
        if (!targetBoard.getId().equals(sourceBoard.getId())) {
            boardMemberRepository
                    .findByBoardIdAndUserId(targetBoard.getId(), userId)
                    .orElseThrow(() ->
                            new CollaborationExceptions.ForbiddenException("User is not a member of the target board"));
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

        return cardMapper.toDto(cardRepository.save(card));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

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

        cardRepository.delete(card);
    }
}

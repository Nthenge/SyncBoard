package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.BoardMemberRepository;
import com.eclectics.collaboration.Tool.repository.CardAssigneeRepository;
import com.eclectics.collaboration.Tool.repository.CardRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.service.CardAssigneeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CardAssigneeServiceImpl implements CardAssigneeService {

    private final CardRepository cardRepository;
    private final CardAssigneeRepository cardAssigneeRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRespository userRepository;

    @Override
    public void removeAssignee(Long cardId, Long requesterId, Long targetUserId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        Long boardId = card.getList().getBoard().getId();

        BoardMember requester = boardMemberRepository
                .findByBoardIdAndUserId(boardId, requesterId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("Not a board member"));

        boolean isSelf = requesterId.equals(targetUserId);
        boolean isAdmin = requester.getRole() == BoardRole.ADMIN;

        if (!isSelf && !isAdmin) {
            throw new CollaborationExceptions.UnauthorizedException(
                    "Only admins can remove other assignees"
            );
        }

        CardAssignee assignee = cardAssigneeRepository
                .findByCardIdAndUserId(cardId, targetUserId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Assignee not found"));

        cardAssigneeRepository.delete(assignee);
    }

    @Override
    public void reassignCard(Long cardId, Long requesterId, Long newUserId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        Long boardId = card.getList().getBoard().getId();

        BoardMember requester = boardMemberRepository
                .findByBoardIdAndUserId(boardId, requesterId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("Not a board member"));

        if (requester.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException(
                    "Only admins can reassign cards"
            );
        }

        User newAssignee = userRepository.findById(newUserId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("User not found"));

        boardMemberRepository
                .findByBoardIdAndUserId(boardId, newUserId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ForbiddenException("User not a board member"));

        cardAssigneeRepository.findByCardId(cardId)
                .forEach(cardAssigneeRepository::delete);

        cardAssigneeRepository.save(
                new CardAssignee(card, newAssignee)
        );
    }
}


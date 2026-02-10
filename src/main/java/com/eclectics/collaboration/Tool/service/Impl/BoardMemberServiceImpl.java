package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.BoardMemberResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.BoardMemberMapper;
import com.eclectics.collaboration.Tool.model.BoardMember;
import com.eclectics.collaboration.Tool.model.BoardRole;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.BoardMemberRepository;
import com.eclectics.collaboration.Tool.repository.BoardsRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.service.BoardMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardMemberServiceImpl implements BoardMemberService {

    private final BoardsRepository boardsRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRespository userRespository;
    private final BoardMemberMapper mapper;

    @Override
    @Transactional
    public List<BoardMemberResponseDTO> addMembers(Long boardId, Long requesterId, List<Long> userIds) {

        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        BoardMember requester = boardMemberRepository
                .findByBoardIdAndUserId(boardId, requesterId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("Not a board member"));

        if (requester.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Only board admins can add members");
        }

        List<BoardMember> membersToSave = new ArrayList<>();

        for (Long userId : userIds) {

            User user = userRespository.findById(userId)
                    .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

            if (!board.getWorkSpaceId().getMembers().contains(user)) {
                throw new CollaborationExceptions.ForbiddenException("User is not a workspace member");
            }

            if (!boardMemberRepository.existsByBoardIdAndUserId(boardId, userId)) {
                BoardMember member = mapper.toEntity(user, board);
                membersToSave.add(member);
            }
        }

        List<BoardMember> savedMembers = boardMemberRepository.saveAll(membersToSave);

        return savedMembers.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void removeMember(Long boardId, Long requesterId, Long targetUserId) {

        BoardMember requester = boardMemberRepository
                .findByBoardIdAndUserId(boardId, requesterId)
                .orElseThrow(() -> new CollaborationExceptions.BadRequestException("Not a board member"));

        if (requester.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Only admins can remove members");
        }

        BoardMember target = boardMemberRepository
                .findByBoardIdAndUserId(boardId, targetUserId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Target not found"));

        if (target.getRole() == BoardRole.ADMIN) {
            long adminCount =
                    boardMemberRepository.countByBoardIdAndRole(boardId, BoardRole.ADMIN);

            if (adminCount <= 1) {
                throw new CollaborationExceptions.ForbiddenException("Board must have at least one admin");
            }
        }

        boardMemberRepository.delete(target);
    }

    @Override
    public void changeRole(
            Long boardId,
            Long requesterId,
            Long targetUserId,
            BoardRole newRole
    ) {

        BoardMember requester = boardMemberRepository
                .findByBoardIdAndUserId(boardId, requesterId)
                .orElseThrow(() -> new CollaborationExceptions.BadRequestException("Not a board member"));

        if (requester.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.ForbiddenException("Only admins can change roles");
        }

        BoardMember target = boardMemberRepository
                .findByBoardIdAndUserId(boardId, targetUserId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Target not found"));

        target.setRole(newRole);
    }
}

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

        requester.assertAdmin();

        List<BoardMember> membersToSave = new ArrayList<>();

        for (Long userId : userIds) {

            User user = userRespository.findById(userId)
                    .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

            board.assertWorkspaceMember(user);

            board.addMember(user);
        }

        List<BoardMember> savedMembers = boardMemberRepository.saveAll(membersToSave);

        return savedMembers.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void removeMember(Long boardId, Long requesterId, Long targetUserId) {

        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        BoardMember requester = boardMemberRepository
                .findByBoardIdAndUserId(boardId, requesterId)
                .orElseThrow(() ->
                        new CollaborationExceptions.BadRequestException("Not a board member"));

        requester.assertAdmin();

        BoardMember target = boardMemberRepository
                .findByBoardIdAndUserId(boardId, targetUserId)
                .orElseThrow(() ->
                        new CollaborationExceptions.ResourceNotFoundException("Target not found"));

        long adminCount =
                boardMemberRepository.countByBoardIdAndRole(boardId, BoardRole.ADMIN);

        board.assertMemberCanBeRemoved(target, adminCount);

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

        requester.assertAdmin();

        BoardMember target = boardMemberRepository
                .findByBoardIdAndUserId(boardId, targetUserId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Target not found"));

        target.changeRole(newRole);
    }
}

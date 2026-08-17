package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.BoardMemberResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.BoardMemberMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.enums.BoardRole;
import com.eclectics.collaboration.Tool.repository.BoardMemberRepository;
import com.eclectics.collaboration.Tool.repository.BoardsRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceMemberRepository;
import com.eclectics.collaboration.Tool.service.BoardMemberService;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BoardMemberServiceImpl implements BoardMemberService {

    private final BoardsRepository boardsRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRespository userRespository;
    private final BoardMemberMapper mapper;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;

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
        List<User> addedUsers = new ArrayList<>();

        for (Long userId : userIds) {

            User user = userRespository.findById(userId)
                    .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

            boolean isWorkspaceMember = workSpaceMemberRepository.existsByWorkspace_IdAndUser_Id(
                    board.getWorkSpaceId().getId(), user.getId());

            if (!isWorkspaceMember) {
                throw new CollaborationExceptions.ForbiddenException(
                        "User is not a workspace member: " + user.getEmail());
            }

            BoardMember member = board.addMember(user);
            membersToSave.add(member);
            addedUsers.add(user);
        }

        List<BoardMember> savedMembers = boardMemberRepository.saveAll(membersToSave);

        for (User user : addedUsers) {
            notifyBoardAdd(requester.getUser(), user, board);
        }

        return savedMembers.stream()
                .map(mapper::toDto)
                .toList();
    }

    private void notifyBoardAdd(User adder, User addedUser, Boards board) {
        try {
            NotificationPreference pref = notificationService.getOrCreatePreferenceEntity(addedUser.getId());

            if (pref.isInAppOnBoardAdd()) {
                notificationService.create(
                        addedUser,
                        Notification.Type.ADDED_TO_BOARD,
                        String.format("%s added you to the board \"%s\"", adder.getFullName(), board.getBoardName()),
                        "BOARD",
                        board.getId()
                );
            }

            if (pref.isEmailOnBoardAdd()) {
                emailService.sendBoardAddedNotification(addedUser.getEmail(), adder.getFullName(), board.getBoardName());
            }
        } catch (Exception e) {
            log.error("Failed to notify user {} of being added to board {}: {}",
                    addedUser.getId(), board.getId(), e.getMessage(), e);
        }
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

    @Override
    public List<BoardMemberResponseDTO> getMembers(Long boardId) {
        return boardMemberRepository.findByBoardId(boardId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}

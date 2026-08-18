package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import com.eclectics.collaboration.Tool.enums.BoardRole;
import com.eclectics.collaboration.Tool.enums.WorkspaceRole;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.WorkSpaceMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.*;
import com.eclectics.collaboration.Tool.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceReposiroty workSpaceReposiroty;
    private final WorkSpaceMapper workSpaceMapper;
    private final UserRespository userRespository;
    private final StarredWorkspaceRepository starredWorkspaceRepository;
    private final CardAssigneeRepository cardAssigneeRepository;
    private final BoardsRepository boardsRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final StarredBoardRepository starredBoardRepository;
    private final ListEntityRepository listEntityRepository;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final UserRecentBoardRepository userRecentBoardRepository;

    @Override
    @Transactional
    public WorkSpaceResponseDTO createWorkspace(User user, WorkSpaceRequestDTO request) {
        WorkSpace ws = workSpaceMapper.toEntity(request, user);
        WorkSpace saved = workSpaceReposiroty.save(ws);

        WorkSpaceMember ownerMember = new WorkSpaceMember();
        ownerMember.setWorkspace(saved);
        ownerMember.setUser(user);
        ownerMember.setRole(WorkspaceRole.ADMIN);
        workSpaceMemberRepository.save(ownerMember);

        log.info("Workspace created id={} by user={}", saved.getId(), user.getEmail());
        return workSpaceMapper.toDto(saved);
    }

    @Override
    public List<WorkSpaceResponseDTO> myWorkspaces() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = (principal instanceof UserDetails) ?
                ((UserDetails)principal).getUsername() : principal.toString();

        User user = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        List<WorkSpace> workspaces = workSpaceReposiroty.findAllByOwnerOrMember(user);

        Set<Long> starredIds = starredWorkspaceRepository.findByUser_Id(user.getId()).stream()
                .map(sw -> sw.getWorkspace().getId())
                .collect(Collectors.toSet());

        return workspaces.stream()
                .map(ws -> {
                    WorkSpaceResponseDTO dto = workSpaceMapper.toDto(ws);
                    dto.setStarred(starredIds.contains(ws.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteWorkspace(Long workspaceId, User user) {
        WorkSpace ws = workSpaceReposiroty.findById(workspaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));

        if (!ws.getWorkSpaceOwnerId().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this workspace");
        }
        workSpaceReposiroty.delete(ws);
    }

    @Transactional
    @Override
    public boolean toggleStarWorkspace(Long workspaceId, Long userId) {
        WorkSpace workspace = workSpaceReposiroty.findById(workspaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));
        User user = userRespository.findById(userId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Optional<StarredWorkspace> existing = starredWorkspaceRepository.findByWorkspace_IdAndUser_Id(workspaceId, userId);
        if (existing.isPresent()) {
            starredWorkspaceRepository.delete(existing.get());
            return false;
        } else {
            starredWorkspaceRepository.save(new StarredWorkspace(workspace, user));
            return true;
        }
    }

    @Override
    public List<WorkSpaceResponseDTO> getStarredWorkspaces(Long userId) {
        return starredWorkspaceRepository.findByUser_Id(userId).stream()
                .map(sw -> {
                    WorkSpaceResponseDTO dto = workSpaceMapper.toDto(sw.getWorkspace());
                    dto.setStarred(true);
                    return dto;
                })
                .toList();
    }

    @Transactional
    @Override
    public void leaveWorkspace(Long workspaceId, User user) {
        WorkSpace ws = workSpaceReposiroty.findById(workspaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));

        WorkSpaceMember member = workSpaceMemberRepository
                .findByWorkspace_IdAndUser_Id(workspaceId, user.getId())
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("You are not a member of this workspace"));

        boolean isAdmin = member.getRole() == WorkspaceRole.ADMIN;
        boolean isOwner = ws.getWorkSpaceOwnerId().getId().equals(user.getId());

        if (isAdmin) {
            long remainingAdminsAfterLeaving =
                    workSpaceMemberRepository.countByWorkspace_IdAndRole(workspaceId, WorkspaceRole.ADMIN) - 1;

            if (remainingAdminsAfterLeaving <= 0) {
                deleteWorkspaceCascade(ws);
                return;
            }

            if (isOwner) {
                WorkSpaceMember newOwnerMember = workSpaceMemberRepository
                        .findFirstByWorkspace_IdAndRoleAndUser_IdNot(workspaceId, WorkspaceRole.ADMIN, user.getId())
                        .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                                "No eligible admin found to transfer ownership"));

                ws.setWorkSpaceOwnerId(newOwnerMember.getUser());
                workSpaceReposiroty.save(ws);

                grantBoardAdminAccessToAllBoards(ws, newOwnerMember.getUser());

                log.info("Workspace ownership transferred id={} from user={} to user={}",
                        ws.getId(), user.getEmail(), newOwnerMember.getUser().getEmail());
            }
        }

        removeMemberFromWorkspace(ws, user);
    }

    private void removeMemberFromWorkspace(WorkSpace ws, User user) {
        Long workspaceId = ws.getId();
        Long userId = user.getId();
        cardAssigneeRepository.deleteByUserIdAndWorkspaceId(userId, workspaceId);
        boardMemberRepository.deleteByUserIdAndWorkspaceId(userId, workspaceId);
        starredWorkspaceRepository.findByWorkspace_IdAndUser_Id(workspaceId, userId)
                .ifPresent(starredWorkspaceRepository::delete);
        starredBoardRepository.deleteByUserIdAndWorkspaceId(userId, workspaceId);
        workSpaceMemberRepository.deleteByWorkspace_IdAndUser_Id(workspaceId, userId);
    }

    private void deleteWorkspaceCascade(WorkSpace ws) {
        Long workspaceId = ws.getId();
        List<Boards> boards = boardsRepository.findAllByWorkSpaceId_Id(workspaceId);

        for (Boards board : boards) {
            Long boardId = board.getId();
            userRecentBoardRepository.deleteByBoard_Id(boardId);
            cardAssigneeRepository.deleteByBoardId(boardId);
            List<ListEntity> lists = listEntityRepository.findByBoard_IdOrderByPosition(boardId);
            listEntityRepository.deleteAll(lists);
            boardMemberRepository.deleteByBoardId(boardId);
            starredBoardRepository.deleteByBoard_Id(boardId);
            boardsRepository.delete(board);
        }
        starredWorkspaceRepository.deleteByWorkspace_Id(workspaceId);
        workSpaceReposiroty.delete(ws);
    }

    private void grantBoardAdminAccessToAllBoards(WorkSpace ws, User user) {
        List<Boards> boards = boardsRepository.findByWorkSpaceId_Id(ws.getId());

        for (Boards board : boards) {
            BoardMember existing = boardMemberRepository
                    .findByBoardIdAndUserId(board.getId(), user.getId())
                    .orElse(null);

            if (existing == null) {
                boardMemberRepository.save(new BoardMember(board, user, BoardRole.ADMIN));
            } else if (existing.getRole() != BoardRole.ADMIN) {
                existing.changeRole(BoardRole.ADMIN);
                boardMemberRepository.save(existing);
            }
        }
    }

    @Transactional
    @Override
    public WorkSpaceResponseDTO updateWorkspace(Long workspaceId, User user, WorkSpaceRequestDTO request) {
        WorkSpace ws = workSpaceReposiroty.findById(workspaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));

        boolean isOwner = ws.getWorkSpaceOwnerId().getId().equals(user.getId());
        boolean isAdminMember = workSpaceMemberRepository
                .findByWorkspace_IdAndUser_Id(workspaceId, user.getId())
                .map(m -> m.getRole() == WorkspaceRole.ADMIN)
                .orElse(false);

        if (!isOwner && !isAdminMember) {
            throw new CollaborationExceptions.ForbiddenException("You do not have permission to edit this workspace");
        }

        if (request.getWorkSpaceName() != null && !request.getWorkSpaceName().isBlank()) {
            ws.setWorkSpaceName(request.getWorkSpaceName());
        }
        ws.setWorkSpaceDescription(request.getWorkSpaceDescription());

        WorkSpace saved = workSpaceReposiroty.save(ws);
        log.info("Workspace updated id={} by user={}", saved.getId(), user.getEmail());
        return workSpaceMapper.toDto(saved);
    }

    @Override
    public WorkSpaceResponseDTO getWorkspaceById(Long id, User user) {
        WorkSpace ws = workSpaceReposiroty.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));

        boolean isOwner = ws.getWorkSpaceOwnerId().getId().equals(user.getId());
        boolean isMember = workSpaceMemberRepository.existsByWorkspace_IdAndUser_Id(id, user.getId());

        if (!isOwner && !isMember) {
            throw new CollaborationExceptions.ForbiddenException("You are not a member of this workspace");
        }

        WorkSpaceResponseDTO dto = workSpaceMapper.toDto(ws);
        dto.setStarred(starredWorkspaceRepository.existsByWorkspace_IdAndUser_Id(id, user.getId()));
        return dto;
    }
}

package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.enums.BoardRole;
import com.eclectics.collaboration.Tool.enums.WorkspaceRole;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.BoardsMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.*;
import com.eclectics.collaboration.Tool.service.BoardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardsServiceImpl implements BoardsService {

    private final UserRespository userRespository;
    private final BoardsRepository boardsRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final BoardsMapper mapper;
    private final WorkSpaceReposiroty workSpaceReposiroty;
    private final SimpMessagingTemplate messagingTemplate;
    private final StarredBoardRepository starredBoardRepository;
    private final StarredWorkspaceRepository starredWorkspaceRepository;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final ListEntityRepository listEntityRepository;
    private final CardAssigneeRepository cardAssigneeRepository;
    private final UserRecentBoardRepository userRecentBoardRepository;


    // ─── CREATE ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public BoardsResponseDTO createBoard(Long workSpaceId, BoardsRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        WorkSpace workSpace = workSpaceReposiroty.findById(workSpaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "Workspace not found with ID: " + workSpaceId));

        boolean isOwner = workSpace.getWorkSpaceOwnerId().getId().equals(currentUser.getId());
        boolean isMember = workSpace.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(currentUser.getId()));

        if (!isOwner && !isMember) {
            throw new CollaborationExceptions.ForbiddenException("You are not a member of this workspace");
        }

        Boards board = mapper.toEntity(dto, workSpace, currentUser);
        Boards savedBoard = boardsRepository.save(board);

        boardMemberRepository.save(new BoardMember(savedBoard, currentUser, BoardRole.ADMIN));

        List<WorkSpaceMember> workspaceAdmins = workSpaceMemberRepository
                .findByWorkspace_IdAndRole(workSpaceId, WorkspaceRole.ADMIN);

        for (WorkSpaceMember wsAdmin : workspaceAdmins) {
            if (wsAdmin.getUser().getId().equals(currentUser.getId())) {
                continue;
            }
            boardMemberRepository.save(new BoardMember(savedBoard, wsAdmin.getUser(), BoardRole.ADMIN));
        }

        messagingTemplate.convertAndSend("/topic/workspace/" + workSpaceId, mapper.toDto(savedBoard));

        return mapper.toDto(savedBoard);
    }

    @Override
    public BoardsResponseDTO getBoardById(Long boardId) {
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        BoardsResponseDTO dto = mapper.toDto(board);
        dto.setStarred(starredBoardRepository.existsByBoard_IdAndUser_Id(board.getId(), currentUser.getId()));
        return dto;
    }

    @Override
    public List<BoardsResponseDTO> getBoardsByWorkspace(Long workSpaceId) {
        if (!workSpaceReposiroty.existsById(workSpaceId)) {
            throw new CollaborationExceptions.ResourceNotFoundException("Workspace not found");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Set<Long> starredIds = starredBoardRepository.findByUser_Id(currentUser.getId()).stream()
                .map(sb -> sb.getBoard().getId())
                .collect(Collectors.toSet());

        return boardsRepository.findAllByWorkSpaceId_Id(workSpaceId)
                .stream()
                .map(board -> {
                    BoardsResponseDTO dto = mapper.toDto(board);
                    dto.setStarred(starredIds.contains(board.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardsResponseDTO> getBoardsForUser(Long userId) {
        Set<Long> starredIds = starredBoardRepository.findByUser_Id(userId).stream()
                .map(sb -> sb.getBoard().getId())
                .collect(Collectors.toSet());

        return boardMemberRepository.findAllByUserId(userId)
                .stream()
                .map(BoardMember::getBoard)
                .map(board -> {
                    BoardsResponseDTO dto = mapper.toDto(board);
                    dto.setStarred(starredIds.contains(board.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public BoardsResponseDTO updateBoard(Long boardId, BoardsRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        // Only board admins can update
        boardMemberRepository
                .findByBoardIdAndUserId(boardId, currentUser.getId())
                .filter(m -> m.getRole() == BoardRole.ADMIN)
                .orElseThrow(() -> new CollaborationExceptions.UnauthorizedException(
                        "Only board admins can update this board"));

        if (dto.getBoardName() != null && !dto.getBoardName().isBlank()) {
            board.setBoardName(dto.getBoardName());
        }

        Boards updated = boardsRepository.save(board);

        BoardsResponseDTO dtoo = mapper.toDto(updated);
        dtoo.setStarred(starredBoardRepository.existsByBoard_IdAndUser_Id(updated.getId(), currentUser.getId()));

        messagingTemplate.convertAndSend(
                "/topic/workspace/" + board.getWorkSpaceId().getId(),
                dto
        );

        return dtoo;
    }

    @Transactional
    @Override
    public void deleteBoardCascade(Boards board) {
        Long boardId = board.getId();

        userRecentBoardRepository.deleteByBoard_Id(boardId);
        cardAssigneeRepository.deleteByBoardId(boardId);

        List<ListEntity> lists = listEntityRepository.findByBoard_IdOrderByPosition(boardId);
        listEntityRepository.deleteAll(lists);

        boardMemberRepository.deleteByBoardId(boardId);
        starredBoardRepository.deleteByBoard_Id(boardId);

        boardsRepository.delete(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        User workspaceOwner = board.getWorkSpaceId().getWorkSpaceOwnerId();

        if (!workspaceOwner.getId().equals(currentUser.getId())) {
            throw new CollaborationExceptions.UnauthorizedException(
                    "You do not have permission to delete boards in this workspace");
        }

        Long workSpaceId = board.getWorkSpaceId().getId();
        deleteBoardCascade(board);

        messagingTemplate.convertAndSend("/topic/workspace/" + workSpaceId + "/delete", boardId);
    }

    @Transactional
    @Override
    public boolean toggleStarBoard(Long boardId, Long userId) {
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));
        User user = userRespository.findById(userId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Optional<StarredBoard> existing = starredBoardRepository.findByBoard_IdAndUser_Id(boardId, userId);
        if (existing.isPresent()) {
            starredBoardRepository.delete(existing.get());
            return false;
        } else {
            starredBoardRepository.save(new StarredBoard(board, user));
            return true;
        }
    }

    @Override
    public List<BoardsResponseDTO> getStarredBoards(Long userId) {
        return starredBoardRepository.findByUser_Id(userId).stream()
                .map(sb -> {
                    BoardsResponseDTO dto = mapper.toDto(sb.getBoard());
                    dto.setStarred(true);
                    return dto;
                })
                .toList();
    }
}

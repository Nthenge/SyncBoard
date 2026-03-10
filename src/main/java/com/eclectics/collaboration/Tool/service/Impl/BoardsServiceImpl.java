package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.BoardsMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.BoardMemberRepository;
import com.eclectics.collaboration.Tool.repository.BoardsRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.BoardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        if (!workSpace.getMembers().contains(currentUser)
                && !workSpace.getWorkSpaceOwnerId().equals(currentUser)) {
            throw new CollaborationExceptions.ForbiddenException("You are not a member of this workspace");
        }

        Boards board = mapper.toEntity(dto, workSpace, currentUser);
        Boards savedBoard = boardsRepository.save(board);

        boardMemberRepository.save(new BoardMember(savedBoard, currentUser, BoardRole.ADMIN));

        messagingTemplate.convertAndSend("/topic/workspace/" + workSpaceId, mapper.toDto(savedBoard));

        return mapper.toDto(savedBoard);
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Override
    public BoardsResponseDTO getBoardById(Long boardId) {
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        return mapper.toDto(board);
    }

    @Override
    public List<BoardsResponseDTO> getBoardsByWorkspace(Long workSpaceId) {
        if (!workSpaceReposiroty.existsById(workSpaceId)) {
            throw new CollaborationExceptions.ResourceNotFoundException("Workspace not found");
        }

        return boardsRepository.findAllByWorkSpaceId_Id(workSpaceId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardsResponseDTO> getBoardsForUser(Long userId) {
        // Returns all boards where the user is a member
        return boardMemberRepository.findAllByUserId(userId)
                .stream()
                .map(BoardMember::getBoard)
                .map(mapper::toDto)
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

        if (dto.getIsStarred() != null) {
            board.setStarred(dto.getIsStarred());
        }

        Boards updated = boardsRepository.save(board);

        messagingTemplate.convertAndSend(
                "/topic/workspace/" + board.getWorkSpaceId().getId(),
                mapper.toDto(updated)
        );

        return mapper.toDto(updated);
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Override
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
        boardsRepository.delete(board);

        messagingTemplate.convertAndSend("/topic/workspace/" + workSpaceId + "/delete", boardId);
    }
}

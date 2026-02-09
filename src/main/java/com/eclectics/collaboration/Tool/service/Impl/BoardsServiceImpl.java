package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.BoardsMapper;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.BoardsRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.BoardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardsServiceImpl implements BoardsService {

    private final UserRespository userRespository;
    private final BoardsRepository boardsRepository;
    private final BoardsMapper mapper;
    private final WorkSpaceReposiroty workSpaceReposiroty;

    @Override
    public BoardsResponseDTO createBoard(Long workSpaceId, BoardsRequestDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        WorkSpace workSpace = workSpaceReposiroty.findById(workSpaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found with ID: " + workSpaceId));

        Boards board = mapper.toEntity(dto, workSpace, currentUser);

        Boards savedBoard = boardsRepository.save(board);

        return mapper.toDto(savedBoard);
    }

    @Override
    public void deleteBoard(Long boardId, User user) {
        Boards boards = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        User workspaceOwner = boards.getWorkSpaceId().getWorkSpaceOwnerId();

        if (!workspaceOwner.getId().equals(user.getId())) {
            throw new CollaborationExceptions.UnauthorizedException("You do not have permission to delete boards in this workspace");
        }

        boardsRepository.delete(boards);
    }

    @Override
    public List<BoardsResponseDTO> getBoardsByWorkspace(Long workSpaceId) {
        if (!workSpaceReposiroty.existsById(workSpaceId)) {
            throw new CollaborationExceptions.ResourceNotFoundException("Workspace not found");
        }

        List<Boards> boards = boardsRepository.findAllByWorkSpaceId_Id(workSpaceId);

        return boards.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

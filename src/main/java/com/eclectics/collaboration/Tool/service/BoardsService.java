package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.model.Boards;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardsService {
    BoardsResponseDTO createBoard(Long workSpaceId, BoardsRequestDTO dto);

    List<BoardsResponseDTO> getBoardsForUser(Long userId);

    @Transactional
    BoardsResponseDTO updateBoard(Long boardId, BoardsRequestDTO dto);

    @Transactional
    void deleteBoardCascade(Boards board);

    void deleteBoard(Long boardId);

    BoardsResponseDTO getBoardById(Long boardId);

    List<BoardsResponseDTO> getBoardsByWorkspace(Long workSpaceId);

    @Transactional
    boolean toggleStarBoard(Long boardId, Long userId);

    List<BoardsResponseDTO> getStarredBoards(Long userId);
}

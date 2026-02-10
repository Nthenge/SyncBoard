package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;

import java.util.List;

public interface BoardsService {
    BoardsResponseDTO createBoard(Long workSpaceId, BoardsRequestDTO dto);
    void deleteBoard(Long boardId);
    List<BoardsResponseDTO> getBoardsByWorkspace(Long workSpaceId);
}

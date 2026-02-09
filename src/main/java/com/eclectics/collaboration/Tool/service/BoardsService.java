package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;

import java.util.List;

public interface BoardsService {
    BoardsResponseDTO createBoard(Long workSpaceId, BoardsRequestDTO dto);
    void deleteBoard(Long boardId, User user);
    List<BoardsResponseDTO> getBoardsByWorkspace(Long workSpaceId);
}

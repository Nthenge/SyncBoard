package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.BoardsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class BoardController {

    private final BoardsService boardService;
    private final UserRespository userRepository;
    private final HttpServletRequest request;

    @PostMapping("/{workSpaceId}/boards")
    public ResponseEntity<Object> createBoard(
            @PathVariable Long workSpaceId,
            @RequestBody BoardsRequestDTO dto) {

        BoardsResponseDTO response = boardService.createBoard(workSpaceId, dto);

        return ResponseHandler.generateResponse(
                "Board created successfully",
                HttpStatus.CREATED,
                response,
                request.getRequestURI()
        );
    }

    @GetMapping("/{workSpaceId}/boards")
    public ResponseEntity<Object> getBoardsByWorkspace(@PathVariable Long workSpaceId) {

        List<BoardsResponseDTO> boards = boardService.getBoardsByWorkspace(workSpaceId);

        return ResponseHandler.generateResponse(
                "Boards fetched successfully",
                HttpStatus.OK,
                boards,
                request.getRequestURI()
        );
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Object> deleteBoard(@PathVariable Long boardId) {

        boardService.deleteBoard(boardId);

        return ResponseHandler.generateResponse(
                "Board deleted successfully",
                HttpStatus.OK,
                null,
                request.getRequestURI()
        );
    }
}

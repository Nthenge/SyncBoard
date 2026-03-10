package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.BoardsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class BoardController {

    private final BoardsService boardService;
    private final HttpServletRequest request;

    // GET /boards
    @GetMapping("/boards")
    public ResponseEntity<Object> getAllBoards(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<BoardsResponseDTO> boards = boardService.getBoardsForUser(userDetails.getId());

        return ResponseHandler.generateResponse(
                "Boards fetched successfully",
                HttpStatus.OK,
                boards,
                request.getRequestURI()
        );
    }

    // GET /boards/{boardId}
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<Object> getBoardById(
            @PathVariable Long boardId) {

        BoardsResponseDTO board = boardService.getBoardById(boardId);

        return ResponseHandler.generateResponse(
                "Board fetched successfully",
                HttpStatus.OK,
                board,
                request.getRequestURI()
        );
    }

    // POST /boards  (workspaceId comes from request body)
    @PostMapping("/boards")
    public ResponseEntity<Object> createBoard(
            @RequestBody BoardsRequestDTO dto) {

        BoardsResponseDTO response = boardService.createBoard(dto.getWorkspaceId(), dto);

        return ResponseHandler.generateResponse(
                "Board created successfully",
                HttpStatus.CREATED,
                response,
                request.getRequestURI()
        );
    }

    // PUT /boards/{boardId}
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<Object> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardsRequestDTO dto) {

        BoardsResponseDTO updated = boardService.updateBoard(boardId, dto);

        return ResponseHandler.generateResponse(
                "Board updated successfully",
                HttpStatus.OK,
                updated,
                request.getRequestURI()
        );
    }

    // DELETE /boards/{boardId}
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Object> deleteBoard(
            @PathVariable Long boardId) {

        boardService.deleteBoard(boardId);

        return ResponseHandler.generateResponse(
                "Board deleted successfully",
                HttpStatus.OK,
                null,
                request.getRequestURI()
        );
    }
}

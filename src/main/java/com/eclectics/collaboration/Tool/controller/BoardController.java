package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.BoardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Tag(name = "Boards", description = "CRUD operations for Boards")
public class BoardController {

    private final BoardsService boardService;
    private final HttpServletRequest request;

    @Operation(summary = "Get all Boards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boards fetched successfully")
    })
    @GetMapping()
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

    @Operation(summary = "Get all Boards by Workspace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boards fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<Object> getBoardsByWorkspace(
            @PathVariable Long workspaceId) {

        List<BoardsResponseDTO> boards = boardService.getBoardsByWorkspace(workspaceId);

        return ResponseHandler.generateResponse(
                "Boards fetched successfully",
                HttpStatus.OK,
                boards,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Get Board by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/{boardId}")
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

    @Operation(summary = "Create a new Board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Board created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/{workspaceId}")
    public ResponseEntity<Object> createBoard(
            @PathVariable Long workspaceId,
            @RequestBody @Valid BoardsRequestDTO dto,
            HttpServletRequest request) {

        BoardsResponseDTO response = boardService.createBoard(workspaceId, dto);

        return ResponseHandler.generateResponse(
                "Board created successfully",
                HttpStatus.CREATED,
                response,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Update an existing Board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board updated successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @PutMapping("/{boardId}")
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

    @Operation(summary = "Delete a Board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @DeleteMapping("/{boardId}")
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

    @Operation(summary = "Toggle star on a board for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Star toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @PostMapping("/{boardId}/star")
    public ResponseEntity<Object> toggleStarBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean starred = boardService.toggleStarBoard(boardId, userDetails.getId());
        return ResponseHandler.generateResponse(
                starred ? "Board starred" : "Board unstarred",
                HttpStatus.OK,
                java.util.Map.of("starred", starred),
                request.getRequestURI()
        );
    }

    @Operation(summary = "Get all boards starred by the current user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Starred boards fetched successfully") })
    @GetMapping("/starred")
    public ResponseEntity<Object> getStarredBoards(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BoardsResponseDTO> boards = boardService.getStarredBoards(userDetails.getId());
        return ResponseHandler.generateResponse("Starred boards fetched", HttpStatus.OK, boards, request.getRequestURI());
    }
}

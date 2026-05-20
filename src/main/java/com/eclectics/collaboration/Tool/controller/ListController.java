package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;
import com.eclectics.collaboration.Tool.dto.ReorderListsRequestDTO;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.ListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Tag(name = "Lists", description = "CRUD and reordering operations for board lists (columns)")
public class ListController {

    private final ListService listService;
    private final HttpServletRequest request;

    @Operation(summary = "Get all lists belonging to a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All board lists fetched successfully")
    })
    @GetMapping("/{boardId}/lists")
    public ResponseEntity<Object> getLists(
            @PathVariable Long boardId) {

        List<ListResponseDTO> lists = listService.getListsByBoard(boardId);
        return ResponseHandler.generateResponse(
                "All board lists",
                HttpStatus.OK,
                lists,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Create a new list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "List created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/lists")
    public ResponseEntity<Object> createList(
            @RequestBody ListRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ListResponseDTO created = listService.createList(dto.getBoardId(), dto, userDetails.getId());
        return ResponseHandler.generateResponse(
                "List created successfully",
                HttpStatus.CREATED,
                created,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Update an existing list's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List updated successfully"),
            @ApiResponse(responseCode = "404", description = "List not found")
    })
    @PutMapping("/lists/{listId}")
    public ResponseEntity<Object> updateList(
            @PathVariable Long listId,
            @RequestBody ListRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ListResponseDTO updated = listService.updateList(listId, userDetails.getId(), dto);
        return ResponseHandler.generateResponse(
                "List updated successfully",
                HttpStatus.OK,
                updated,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Delete a list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List deleted successfully"),
            @ApiResponse(responseCode = "404", description = "List not found")
    })
    @DeleteMapping("/lists/{listId}")
    public ResponseEntity<Object> deleteList(
            @PathVariable Long listId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        listService.deleteList(listId, userDetails.getId());
        return ResponseHandler.generateResponse(
                "List deleted successfully",
                HttpStatus.OK,
                null,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Reorder lists within a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lists reordered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid list ordering array")
    })
    @PutMapping("/{boardId}/lists/reorder")
    public ResponseEntity<Object> reorderLists(
            @PathVariable Long boardId,
            @RequestBody ReorderListsRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ListResponseDTO> reordered = listService.reorderLists(boardId, dto.getListIds(), userDetails.getId());
        return ResponseHandler.generateResponse(
                "Lists reordered successfully",
                HttpStatus.OK,
                reordered,
                request.getRequestURI()
        );
    }
}
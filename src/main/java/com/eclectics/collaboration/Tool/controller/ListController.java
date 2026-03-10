package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;
import com.eclectics.collaboration.Tool.dto.ReorderListsRequestDTO;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.ListService;
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
public class ListController {

    private final ListService listService;
    private final HttpServletRequest request;

    // GET /boards/{boardId}/lists
    @GetMapping("/boards/{boardId}/lists")
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

    // POST /lists
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

    // PUT /lists/{listId}
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

    // DELETE /lists/{listId}
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

    // PUT /boards/{boardId}/lists/reorder
    @PutMapping("/boards/{boardId}/lists/reorder")
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

package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;
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
@RequestMapping("/list")
@RequiredArgsConstructor
public class ListController {

    private final ListService listService;
    private HttpServletRequest request;

    @PostMapping("/create")
    public ResponseEntity<Object> createList(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ListRequestDTO dto) {

        ListResponseDTO created = listService.createList(boardId, dto, user.getId());
        return ResponseHandler.generateResponse("List created", HttpStatus.CREATED,created,request.getRequestURI());
    }

    @GetMapping("/boardLists")
    public ResponseEntity<Object> getLists(
            @PathVariable Long boardId) {

        List<ListResponseDTO> lists = listService.getListsByBoard(boardId);
        return ResponseHandler.generateResponse("All board Lists", HttpStatus.OK,lists,request.getRequestURI());
    }
}


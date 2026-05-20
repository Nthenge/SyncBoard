package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.AddBoardMemberRequestDTO;
import com.eclectics.collaboration.Tool.enums.BoardRole;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.BoardMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board/{boardId}/members")
@Tag(name = "Board Members", description = "Operations for managing board members and roles")
public class BoardMemberController {

    private final BoardMemberService service;

    @Operation(summary = "Add members to a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<?> addMembers(
            @PathVariable Long boardId,
            @RequestBody AddBoardMemberRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        service.addMembers(boardId, user.getId(), request.getUserIds());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a member from a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member removed successfully"),
            @ApiResponse(responseCode = "404", description = "Board or member not found")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeMember(
            @PathVariable Long boardId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        service.removeMember(boardId, user.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change a board member's role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Board or member not found")
    })
    @PatchMapping("/{userId}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable Long boardId,
            @PathVariable Long userId,
            @RequestParam BoardRole role,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        service.changeRole(boardId, user.getId(), userId, role);
        return ResponseEntity.ok().build();
    }
}
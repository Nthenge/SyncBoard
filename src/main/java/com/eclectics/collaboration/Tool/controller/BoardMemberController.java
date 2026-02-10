package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.AddBoardMemberRequestDTO;
import com.eclectics.collaboration.Tool.model.BoardRole;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.BoardMemberService;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board/{boardId}/members")
public class BoardMemberController {

    private final BoardMemberService service;

    @PostMapping
    public ResponseEntity<?> addMembers(
            @PathVariable Long boardId,
            @RequestBody AddBoardMemberRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        service.addMembers(boardId, user.getId(), request.getUserIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeMember(
            @PathVariable Long boardId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        service.removeMember(boardId, user.getId(), userId);
        return ResponseEntity.noContent().build();
    }

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

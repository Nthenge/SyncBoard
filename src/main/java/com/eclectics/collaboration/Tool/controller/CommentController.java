package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.CommentRequestDTO;
import com.eclectics.collaboration.Tool.dto.CommentResponseDTO;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards/{cardId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long cardId,
            @RequestBody CommentRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(cardId, dto, userDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long cardId) {
        return ResponseEntity.ok(commentService.getCommentsByCard(cardId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long cardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
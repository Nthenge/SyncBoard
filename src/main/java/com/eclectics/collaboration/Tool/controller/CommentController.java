package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.CommentResponseDTO;
import com.eclectics.collaboration.Tool.dto.CreateCommentRequestDTO;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards/{cardId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long cardId,
            @RequestBody CreateCommentRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                commentService.addComment(cardId, userDetails.getUser(), dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long cardId) {
        return ResponseEntity.ok(commentService.getCardComments(cardId));
    }
}


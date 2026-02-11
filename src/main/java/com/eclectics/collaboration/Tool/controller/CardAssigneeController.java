package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.CardAssigneeRequestDTO;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.CardAssigneeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/{cardId}/assignees")
public class CardAssigneeController {

    private final CardAssigneeService cardAssigneeService;

    public CardAssigneeController(CardAssigneeService cardAssigneeService) {
        this.cardAssigneeService = cardAssigneeService;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeAssignee(
            @PathVariable Long cardId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cardAssigneeService.removeAssignee(
                cardId,
                userDetails.getId(),
                userId
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reassign")
    public ResponseEntity<Void> reassignCard(
            @PathVariable Long cardId,
            @RequestBody CardAssigneeRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cardAssigneeService.reassignCard(
                cardId,
                userDetails.getId(),
                dto.getUserId()
        );
        return ResponseEntity.ok().build();
    }
}


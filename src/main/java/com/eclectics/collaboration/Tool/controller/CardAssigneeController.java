package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.CardAssigneeRequestDTO;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.CardAssigneeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards/{cardId}/assignees")
@Tag(name = "Card Assignees", description = "Operations for managing assignees on task cards")
public class CardAssigneeController {

    private final CardAssigneeService cardAssigneeService;

    public CardAssigneeController(CardAssigneeService cardAssigneeService) {
        this.cardAssigneeService = cardAssigneeService;
    }

    @Operation(summary = "Remove an assignee from a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assignee removed successfully"),
            @ApiResponse(responseCode = "404", description = "Card or assignee not found")
    })
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

    @Operation(summary = "Reassign a card to a different user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card reassigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
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
package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardMoveRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import com.eclectics.collaboration.Tool.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "CRUD and movement operations for task cards")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Get all cards belonging to a list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully")
    })
    @GetMapping("/lists/{listId}/cards")
    public ResponseEntity<List<CardResponseDTO>> getCardsByList(@PathVariable Long listId) {
        return ResponseEntity.ok(cardService.getCardsByList(listId));
    }

    @Operation(summary = "Get a card by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.getCardById(cardId));
    }

    @Operation(summary = "Create a new card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping()
    public ResponseEntity<CardResponseDTO> createCard(
            @RequestBody CardRequestDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.createCard(dto.getListId(), dto, userId));
    }

    @Operation(summary = "Update an existing card's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PutMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> updateCard(
            @PathVariable Long cardId,
            @RequestBody CardRequestDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(cardService.updateCard(cardId, userId, dto));
    }

    @Operation(summary = "Move a card to a different list or position")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card moved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid movement data")
    })
    @PutMapping("/{cardId}/move")
    public ResponseEntity<CardResponseDTO> moveCard(
            @PathVariable Long cardId,
            @RequestBody CardMoveRequestDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(cardService.moveCard(cardId, dto, userId));
    }

    @Operation(summary = "Delete a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardId,
            @RequestAttribute("userId") Long userId) {
        cardService.deleteCard(cardId, userId);
        return ResponseEntity.noContent().build();
    }
}
package com.eclectics.collaboration.Tool.controller;


import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardMoveRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import com.eclectics.collaboration.Tool.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/lists/{listId}/cards")
    public ResponseEntity<List<CardResponseDTO>> getCardsByList(@PathVariable Long listId) {
        return ResponseEntity.ok(cardService.getCardsByList(listId));
    }


    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.getCardById(cardId));
    }

    @PostMapping()
    public ResponseEntity<CardResponseDTO> createCard(
            @RequestBody CardRequestDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.createCard(dto.getListId(), dto, userId));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> updateCard(
            @PathVariable Long cardId,
            @RequestBody CardRequestDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(cardService.updateCard(cardId, userId, dto));
    }

    @PutMapping("/{cardId}/move")
    public ResponseEntity<CardResponseDTO> moveCard(
            @PathVariable Long cardId,
            @RequestBody CardMoveRequestDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(cardService.moveCard(cardId, dto, userId));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardId,
            @RequestAttribute("userId") Long userId) {
        cardService.deleteCard(cardId, userId);
        return ResponseEntity.noContent().build();
    }
}


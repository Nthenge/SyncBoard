package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.CardMoveRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardService {

    CardResponseDTO createCard(Long listId, CardRequestDTO dto, Long userId);

    List<CardResponseDTO> getCardsByList(Long listId);

    CardResponseDTO getCardById(Long cardId);

    CardResponseDTO updateCard(Long cardId, Long userId, CardRequestDTO dto);

    CardResponseDTO moveCard(Long cardId, CardMoveRequestDTO dto, Long userId);

    void deleteCard(Long cardId, Long userId);
}

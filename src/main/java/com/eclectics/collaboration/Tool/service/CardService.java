package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;

import java.util.List;

public interface CardService {

    CardResponseDTO createCard(Long listId, CardRequestDTO dto, Long userId);
    List<CardResponseDTO> getCardsByList(Long listId);

}

package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.CardMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.BoardMemberRepository;
import com.eclectics.collaboration.Tool.repository.CardRepository;
import com.eclectics.collaboration.Tool.repository.ListEntityRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ListEntityRepository listRepository;
    private final UserRespository userRepository;
    private final CardMapper cardMapper;
    private final BoardMemberRepository boardMemberRepository;

    @Override
    @Transactional
    public CardResponseDTO createCard(Long listId, CardRequestDTO dto, Long userId) {

        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("List not found"));

        Boards board = list.getBoard();

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(board.getId(), userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Card card = cardMapper.toEntity(dto, list, user);

        if (card.getPosition() == null) {
            Integer maxPos = cardRepository.findByListIdOrderByPosition(listId).stream()
                    .map(Card::getPosition)
                    .max(Integer::compareTo)
                    .orElse(0);
            card.setPosition(maxPos + 1);
        }

        Card saved = cardRepository.save(card);
        return cardMapper.toDto(saved);
    }

    @Override
    public List<CardResponseDTO> getCardsByList(Long listId) {
        return cardRepository.findByListIdOrderByPosition(listId)
                .stream()
                .map(cardMapper::toDto)
                .toList();
    }
}


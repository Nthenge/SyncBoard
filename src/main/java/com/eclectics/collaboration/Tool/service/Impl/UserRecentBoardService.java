package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.RecentBoardResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.*;
import com.eclectics.collaboration.Tool.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRecentBoardService {

    private final UserRecentBoardRepository recentBoardRepository;
    private final UserRespository userRepository;
    private final BoardsRepository boardRepository;
    private final ListEntityRepository listRepository;
    private final CardRepository cardRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public void trackActivity(String token, Long boardId, Long listId, Long cardId) {
        User user = getUserByToken(token);
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        ListEntity list = listId != null
                ? listRepository.findById(listId).orElse(null)
                : null;

        Card card = cardId != null
                ? cardRepository.findById(cardId).orElse(null)
                : null;

        UserRecentBoard entry = recentBoardRepository
                .findByUserBoardListCard(user.getId(), boardId, listId, cardId)
                .orElseGet(() -> new UserRecentBoard(user, board, list, card));

        entry.setLastAccessedAt(LocalDateTime.now());
        recentBoardRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<RecentBoardResponseDTO> getRecentBoards(String token, int limit) {
        User user = getUserByToken(token);
        return recentBoardRepository.findRecentByUserId(user.getId(), PageRequest.of(0, limit));
    }

    private User getUserByToken(String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));
    }
}
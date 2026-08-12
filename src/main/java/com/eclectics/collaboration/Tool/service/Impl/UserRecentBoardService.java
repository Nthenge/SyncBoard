package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.RecentBoardResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.UserRecentBoard;
import com.eclectics.collaboration.Tool.repository.BoardsRepository;
import com.eclectics.collaboration.Tool.repository.UserRecentBoardRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
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
    private final JwtUtil jwtUtil;

    @Transactional
    public void trackBoardAccess(String token, Long boardId) {
        User user = getUserByToken(token);
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        UserRecentBoard entry = recentBoardRepository.findByUserIdAndBoardId(user.getId(), boardId)
                .orElseGet(() -> new UserRecentBoard(user, board));

        entry.setLastAccessedAt(LocalDateTime.now());
        recentBoardRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<RecentBoardResponseDTO> getRecentBoards(String token, int limit) {
        User user = getUserByToken(token);
        return recentBoardRepository.findRecentBoardsByUserId(user.getId(), PageRequest.of(0, limit));
    }

    private User getUserByToken(String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));
    }
}

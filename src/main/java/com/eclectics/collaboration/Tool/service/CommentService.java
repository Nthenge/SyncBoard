package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.CommentRequestDTO;
import com.eclectics.collaboration.Tool.dto.CommentResponseDTO;
import com.eclectics.collaboration.Tool.dto.CreateCommentRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {
    @Transactional
    CommentResponseDTO createComment(Long cardId, CommentRequestDTO dto, Long userId);

    List<CommentResponseDTO> getCommentsByCard(Long cardId);

    @Transactional
    void deleteComment(Long commentId, Long userId);
}

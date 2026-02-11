package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.CommentResponseDTO;
import com.eclectics.collaboration.Tool.dto.CreateCommentRequestDTO;
import com.eclectics.collaboration.Tool.model.User;

import java.util.List;

public interface CommentService {
    CommentResponseDTO addComment(Long cardId, User user, CreateCommentRequestDTO dto);

    List<CommentResponseDTO> getCardComments(Long cardId);
}

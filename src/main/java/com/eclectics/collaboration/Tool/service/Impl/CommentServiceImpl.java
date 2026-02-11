package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.CommentResponseDTO;
import com.eclectics.collaboration.Tool.dto.CreateCommentRequestDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.CommentMapper;
import com.eclectics.collaboration.Tool.model.Card;
import com.eclectics.collaboration.Tool.model.Comment;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.BoardMemberRepository;
import com.eclectics.collaboration.Tool.repository.CardRepository;
import com.eclectics.collaboration.Tool.repository.CommentRepository;
import com.eclectics.collaboration.Tool.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

        private final CommentRepository commentRepository;
        private final CardRepository cardRepository;
        private final BoardMemberRepository boardMemberRepository;
        private final CommentMapper commentMapper;


        @Override
        public CommentResponseDTO addComment(Long cardId, User user, CreateCommentRequestDTO dto) {

            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Card not found"));

            Long boardId = card.getList().getBoard().getId();

            boardMemberRepository
                    .findByBoardIdAndUserId(boardId, user.getId())
                    .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("Not a board member"));

            Comment comment = new Comment(card, user, dto.getContent());
            Comment saved = commentRepository.save(comment);

            return commentMapper.toDto(saved);
        }

        @Override
        public List<CommentResponseDTO> getCardComments(Long cardId) {
            return commentRepository.findByCardIdOrderByCreatedAtAsc(cardId)
                    .stream()
                    .map(commentMapper::toDto)
                    .toList();
        }

}

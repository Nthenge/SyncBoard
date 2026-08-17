package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.CommentRequestDTO;
import com.eclectics.collaboration.Tool.dto.CommentResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.*;
import com.eclectics.collaboration.Tool.service.CommentService;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMentionRepository commentMentionRepository;
    private final CardRepository cardRepository;
    private final UserRespository userRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public CommentResponseDTO createComment(Long cardId, CommentRequestDTO dto, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Card not found"));

        Long boardId = card.getList().getBoard().getId();

        boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setCard(card);
        comment.setAuthor(author);
        comment.setContent(dto.getContent());
        Comment saved = commentRepository.save(comment);

        List<User> mentioned = dto.getMentionedUserIds() != null
                ? dto.getMentionedUserIds().stream()
                .map(uid -> userRepository.findById(uid).orElse(null))
                .filter(u -> u != null && !u.getId().equals(userId))
                .toList()
                : List.of();

        for (User user : mentioned) {
            commentMentionRepository.save(new CommentMention(saved, user));

            try {
                NotificationPreference pref = notificationService.getOrCreatePreferenceEntity(user.getId());

                if (pref.isInAppOnMention()) {
                    notificationService.create(
                            user,
                            Notification.Type.MENTIONED,
                            String.format("%s mentioned you in a comment on \"%s\"", author.getFullName(), card.getTitle()),
                            "CARD",
                            card.getId()
                    );
                }

                if (pref.isEmailOnMention()) {
                    String snippet = dto.getContent().length() > 140
                            ? dto.getContent().substring(0, 140) + "..."
                            : dto.getContent();
                    emailService.sendMentionNotification(user.getEmail(), author.getFullName(), card.getTitle(), snippet);
                }
            } catch (Exception e) {
                log.error("Failed to notify mentioned user {} on card {}: {}", user.getId(), card.getId(), e.getMessage(), e);
            }
        }

        return toDto(saved);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByCard(Long cardId) {
        return commentRepository.findByCardIdOrderByCreatedAtAsc(cardId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Comment not found"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CollaborationExceptions.UnauthorizedException("Cannot delete another user's comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponseDTO toDto(Comment comment) {
        List<CommentResponseDTO.MentionDTO> mentions = commentMentionRepository.findByCommentId(comment.getId())
                .stream()
                .map(m -> new CommentResponseDTO.MentionDTO(m.getMentionedUser().getId(), m.getMentionedUser().getFullName()))
                .toList();

        return new CommentResponseDTO(
                comment.getId(),
                comment.getCard().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getFullName(),
                comment.getContent(),
                comment.getCreatedAt(),
                mentions
        );
    }
}
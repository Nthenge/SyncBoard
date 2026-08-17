package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {
    List<CommentMention> findByCommentId(Long commentId);
    List<CommentMention> findByMentionedUserIdAndComment_CreatedAtBetween(
            Long mentionedUserId, LocalDateTime start, LocalDateTime end);
}
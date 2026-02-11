package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCardIdOrderByCreatedAtAsc(Long cardId);
}


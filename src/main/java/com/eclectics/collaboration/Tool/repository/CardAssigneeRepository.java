package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.CardAssignee;
import com.eclectics.collaboration.Tool.model.CardAssigneeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardAssigneeRepository extends JpaRepository<CardAssignee, Long> {

    boolean existsByCardIdAndUserId(Long cardId, Long userId);

    Optional<CardAssignee> findByCardIdAndUserId(Long cardId, Long userId);

    List<CardAssignee> findByCardId(Long cardId);

    void deleteByCardIdAndUserId(Long cardId, Long userId);
}




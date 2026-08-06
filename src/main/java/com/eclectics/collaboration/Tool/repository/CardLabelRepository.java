package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.CardLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardLabelRepository extends JpaRepository<CardLabel, Long> {
    List<CardLabel> findByCardId(Long cardId);
    Optional<CardLabel> findByCardIdAndLabelId(Long cardId, Long labelId);
    boolean existsByCardIdAndLabelId(Long cardId, Long labelId);
    void deleteByCardIdAndLabelId(Long cardId, Long labelId);
}

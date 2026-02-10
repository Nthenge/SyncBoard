package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByListIdOrderByPosition(Long listId);
}

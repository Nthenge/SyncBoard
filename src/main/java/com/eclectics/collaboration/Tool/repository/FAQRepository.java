package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.FAQs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQs, Long> {

    List<FAQs> findByActiveTrue();

    boolean existsByQuestionIgnoreCase(String question);
}

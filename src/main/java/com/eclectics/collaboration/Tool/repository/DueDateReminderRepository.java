package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.DueDateReminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DueDateReminderRepository extends JpaRepository<DueDateReminder, Long> {
    boolean existsByCardId(Long cardId);
}

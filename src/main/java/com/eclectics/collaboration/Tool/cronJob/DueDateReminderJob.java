package com.eclectics.collaboration.Tool.cronJob;

import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.CardAssigneeRepository;
import com.eclectics.collaboration.Tool.repository.CardRepository;
import com.eclectics.collaboration.Tool.repository.DueDateReminderRepository;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DueDateReminderJob {

    private final CardRepository cardRepository;
    private final CardAssigneeRepository cardAssigneeRepository;
    private final DueDateReminderRepository dueDateReminderRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    private static final int REMINDER_WINDOW_HOURS = 6;

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    @Transactional
    public void sendDueSoonReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusHours(REMINDER_WINDOW_HOURS);

        List<Card> dueSoonCards = cardRepository.findByDueDateBetween(now, windowEnd);

        for (Card card : dueSoonCards) {
            try {
                processCard(card);
            } catch (Exception e) {
                log.error("Failed to process due-soon reminder for card {}: {}", card.getId(), e.getMessage(), e);
            }
        }
    }

    private void processCard(Card card) {
        if (dueDateReminderRepository.existsByCardId(card.getId())) {
            return;
        }

        List<CardAssignee> assignees = cardAssigneeRepository.findByCardId(card.getId());

        for (CardAssignee cardAssignee : assignees) {
            notifyAssignee(cardAssignee.getUser(), card);
        }

        dueDateReminderRepository.save(
                DueDateReminder.builder()
                        .cardId(card.getId())
                        .remindedAt(LocalDateTime.now())
                        .build()
        );
    }

    private void notifyAssignee(User user, Card card) {
        try {
            NotificationPreference pref = notificationService.getOrCreatePreferenceEntity(user.getId());

            if (pref.isInAppOnDueSoon()) {
                notificationService.create(
                        user,
                        Notification.Type.DUE_SOON,
                        String.format("\"%s\" is due soon", card.getTitle()),
                        "CARD",
                        card.getId()
                );
            }

            if (pref.isEmailOnDueSoon()) {
                emailService.sendDueSoonNotification(user.getEmail(), card.getTitle(), card.getDueDate());
            }
        } catch (Exception e) {
            log.error("Failed to notify user {} of due-soon card {}: {}", user.getId(), card.getId(), e.getMessage(), e);
        }
    }
}

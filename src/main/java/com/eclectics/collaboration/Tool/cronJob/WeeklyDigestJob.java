package com.eclectics.collaboration.Tool.cronJob;

import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.CardAssigneeRepository;
import com.eclectics.collaboration.Tool.repository.CommentMentionRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklyDigestJob {

    private final UserRespository userRepository;
    private final CardAssigneeRepository cardAssigneeRepository;
    private final CommentMentionRepository commentMentionRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    // Every Friday at 3:00 PM
    @Scheduled(cron = "0 0 15 * * FRI")
    @Transactional
    public void sendWeeklyDigests() {
        List<User> users = userRepository.findAll();
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();

        for (User user : users) {
            try {
                processUser(user, weekAgo, now);
            } catch (Exception e) {
                log.error("Failed to build/send weekly digest for user {}: {}", user.getId(), e.getMessage(), e);
            }
        }
    }

    private void processUser(User user, LocalDateTime weekAgo, LocalDateTime now) {
        NotificationPreference pref = notificationService.getOrCreatePreferenceEntity(user.getId());
        if (!pref.isWeeklyDigest()) {
            return;
        }

        List<CardAssignee> assignedCards = cardAssigneeRepository.findByUserId(user.getId());

        List<CommentMention> mentions = commentMentionRepository
                .findByMentionedUserIdAndComment_CreatedAtBetween(user.getId(), weekAgo, now);

        if (assignedCards.isEmpty() && mentions.isEmpty()) {
            return; // nothing to report, skip per your earlier decision
        }

        List<String> assignedCardTitles = assignedCards.stream()
                .map(ca -> ca.getCard().getTitle())
                .collect(Collectors.toList());

        List<String> mentionSummaries = mentions.stream()
                .map(m -> String.format("%s mentioned you on \"%s\"",
                        m.getComment().getAuthor().getFullName(),
                        m.getComment().getCard().getTitle()))
                .collect(Collectors.toList());

        emailService.sendWeeklyDigest(user.getEmail(), user.getFullName(), assignedCardTitles, mentionSummaries);
    }
}
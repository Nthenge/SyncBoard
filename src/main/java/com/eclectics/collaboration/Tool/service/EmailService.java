package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.InviteRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

public interface EmailService {
    void sendAccountConfirmationEmail(String to, String confirmLink);
    void sendPasswordResetEmail(String to, String resetLink);

    void sendMentionNotification(String to, String mentionerName, String cardTitle, String commentSnippet);

    void inviteUsers(User owner, InviteRequestDTO inviteDto, Long workspaceId);
    void sendInvitationEmail(String to, String token, String workspaceName);
    void sendInviteRejectedEmail(String ownerEmail, String inviteeEmail, String workspaceName);
    void sendNewSupportNotification(String submitterName, String submitterEmail, String issueName, String message);

    void sendCardAssignedNotification(String to, String assignerName, String cardTitle);

    void sendBoardAddedNotification(String to, String adderName, String boardName);

    void sendDueSoonNotification(String to, String cardTitle, LocalDateTime dueDate);

    void sendWeeklyDigest(String to, String userName, List<String> assignedCardTitles, List<String> mentionSummaries);
}


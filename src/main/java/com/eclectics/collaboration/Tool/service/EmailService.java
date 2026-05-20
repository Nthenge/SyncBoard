package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.InviteRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

public interface EmailService {
    void sendAccountConfirmationEmail(String to, String confirmLink);
    void sendPasswordResetEmail(String to, String resetLink);
    void inviteUsers(User owner, InviteRequestDTO inviteDto, Long workspaceId);
    void sendInvitationEmail(String to, String token, String workspaceName);
    void sendInviteRejectedEmail(String ownerEmail, String inviteeEmail, String workspaceName);
    void sendNewSupportNotification(String submitterName, String submitterEmail, String issueName, String message);
}


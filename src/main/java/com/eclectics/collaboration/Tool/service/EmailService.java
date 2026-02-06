package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.InviteRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

public interface EmailService {

    void sendAccountConfirmationEmail(String to, String confirmLink);

    void sendPasswordResetEmail(String to, String resetLink);

    @Transactional
    void inviteUsers(User owner, InviteRequestDTO inviteDto) throws AccessDeniedException;
}


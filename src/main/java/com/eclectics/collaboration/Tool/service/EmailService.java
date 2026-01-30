package com.eclectics.collaboration.Tool.service;

public interface EmailService {

    void sendAccountConfirmationEmail(String to, String confirmLink);

    void sendPasswordResetEmail(String to, String resetLink);
}


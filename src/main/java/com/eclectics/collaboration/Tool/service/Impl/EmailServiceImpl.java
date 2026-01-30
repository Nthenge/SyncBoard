package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendAccountConfirmationEmail(String to, String confirmLink) {
        String subject = "Confirm your account";
        String text = "Hi,\n\nThank you for registering!\n"
                + "Please confirm your account by clicking the link below:\n"
                + confirmLink + "\n\nBest regards,\nCollaboration Tool Team";

        sendEmail(to, subject, text);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Reset your password";
        String text = "Hi,\n\nWe received a request to reset your password.\n"
                + "You can reset your password using the link below:\n"
                + resetLink + "\n\nIf you didn't request this, please ignore this email.\n"
                + "Best regards,\nCollaboration Tool Team";

        sendEmail(to, subject, text);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}

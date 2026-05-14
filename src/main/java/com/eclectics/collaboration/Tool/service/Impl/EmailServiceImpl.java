package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.InviteRequestDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.Invitation;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.InvitationRepository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final WorkSpaceReposiroty workspaceRepository;
    private final InvitationRepository invitationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendAccountConfirmationEmail(String to, String confirmLink) {
        String subject = "SYNCBOARD ACCOUNT CONFIRMATION";
        String text = "Hello,\n\nThank you for registering!\n"
                + "Please confirm your account by clicking the link below:\n"
                + confirmLink + "\n\nBest regards,\nSYNCBOARD";
        sendEmail(to, subject, text);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink){
        String subject = "SYNCBOARD RESET PASSWORD";
        String text = "Hello,\n\nWe received a request to reset your password.\n"
                + "You can reset your password using the link below:\n"
                + resetLink + "\n\nIf you didn't request this, please ignore this email.\n"
                + "Best regards,\nSYNCBOARD";
        sendEmail(to, subject, text);
    }

    @Transactional
    @Override
    public void inviteUsers(User owner, InviteRequestDTO inviteDto){
        WorkSpace workspace = workspaceRepository.findById(inviteDto.getWorkspaceId())
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));

        if (!workspace.getWorkSpaceOwnerId().getId().equals(owner.getId())) {
            throw new CollaborationExceptions.UnauthorizedException("Only the owner can invite others");
        }

        for (String email : inviteDto.getEmails()) {
            String token = UUID.randomUUID().toString();

            Invitation invite = new Invitation();
            invite.setEmail(email);
            invite.setWorkspace(workspace);
            invite.setInviteToken(token);
            invite.setExpiryDate(LocalDateTime.now().plusDays(7));

            invitationRepository.save(invite);

            sendInvitationEmail(email, token, workspace.getWorkSpaceName());
        }
    }

    @Override
    public void sendInvitationEmail(String to, String token, String workspaceName){
        String subject = "SYNCHBOARD WORKSPACE INVITE";
        String text = "Hello,\n\n"
                + "You've been invited to join the workspace: " + workspaceName + "\n\n"
                + "Accept:  http://syncboard-frontend-814g.onrender.com/accept-invite?token=" + token + "\n"
                + "Decline: http://syncboard-frontend-814g.onrender.com/reject-invite?token=" + token + "\n\n"
                + "This invite expires in 7 days.\n\n"
                + "Best regards,\nSYNCBOARD";
        sendEmail(to, subject, text);
    }

        @Override
        public void sendInviteRejectedEmail(String ownerEmail, String inviteeEmail, String workspaceName){
            String subject = "SYNCBOARD INVITE DECLINE";
            String text = "Hello,\n\n"
                    + inviteeEmail + " has declined your invitation to join the workspace: "
                    + workspaceName + ".\n\n"
                    + "You may invite someone else if needed.\n\n"
                    + "Best regards,\nSYNCBOARD";
            sendEmail(ownerEmail, subject, text);
        }

    private void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("syncboardke@gmail.com", "SYNCBOARD KENYA");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send email to: " + to, e);
        }
    }
}

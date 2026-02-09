package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.InviteRequestDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.Invitation;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.InvitationRepository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final WorkSpaceReposiroty workspaceRepository;
    private final InvitationRepository invitationRepository;


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

    @Transactional
    @Override
    public void inviteUsers(User owner, InviteRequestDTO inviteDto) throws AccessDeniedException {
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

    public void sendInvitationEmail(String to, String token, String workspaceName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("You're invited to " + workspaceName);
        message.setText("Click here to join: http://yourapp.com/accept-invite?token=" + token);
        mailSender.send(message);
    }
}

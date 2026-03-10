package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.InvitationResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.Invitation;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.InvitationRepository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final WorkSpaceReposiroty workSpaceRepository;
    private final EmailService emailService;

    @Transactional
    @Override
    public void acceptInvite(String token, User invitee) {
        Invitation invite = invitationRepository.findByInviteToken(token)
                .orElseThrow(() -> new CollaborationExceptions.BadRequestException("Invalid or non-existent invitation token."));

        if (invite.getExpiryDate().isBefore(LocalDateTime.now())) {
            invitationRepository.delete(invite);
            throw new CollaborationExceptions.BadRequestException("This invitation has expired.");
        }

        if (!invite.getEmail().equalsIgnoreCase(invitee.getEmail())) {
            throw new CollaborationExceptions.BadRequestException("This invite was sent to a different email address.");
        }

        WorkSpace ws = invite.getWorkspace();
        ws.getMembers().add(invitee);

        workSpaceRepository.save(ws);
        invitationRepository.delete(invite);
    }

    @Transactional
    @Override
    public void rejectInvite(String token, User invitee) {
        Invitation invite = invitationRepository.findByInviteToken(token)
                .orElseThrow(() -> new CollaborationExceptions.BadRequestException("Invalid or non-existent invitation token."));

        if (invite.getExpiryDate().isBefore(LocalDateTime.now())) {
            invitationRepository.delete(invite);
            throw new CollaborationExceptions.BadRequestException("This invitation has expired.");
        }

        // Only the intended recipient can reject
        if (!invite.getEmail().equalsIgnoreCase(invitee.getEmail())) {
            throw new CollaborationExceptions.BadRequestException("This invite was sent to a different email address.");
        }

        WorkSpace workspace = invite.getWorkspace();
        User owner = workspace.getWorkSpaceOwnerId();

        // Delete the invitation before sending email (so it's gone even if email fails)
        invitationRepository.delete(invite);

        // Notify the workspace owner
        emailService.sendInviteRejectedEmail(owner.getEmail(), invitee.getEmail(), workspace.getWorkSpaceName());
    }


    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public void deleteInvitation(Long invitationId, User requester) {
        Invitation invite = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Invitation not found."));

        WorkSpace workspace = invite.getWorkspace();

        // Only the workspace owner can manually delete an invite
        if (!workspace.getWorkSpaceOwnerId().getId().equals(requester.getId())) {
            throw new CollaborationExceptions.UnauthorizedException("Only the workspace owner can delete invitations.");
        }

        invitationRepository.delete(invite);
    }

    // ─── GET ALL FOR WORKSPACE ────────────────────────────────────────────────

    @Override
    public List<InvitationResponseDTO> getWorkspaceInvitations(Long workspaceId, User requester) {
        WorkSpace workspace = workSpaceRepository.findById(workspaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found."));

        // Only the workspace owner can view all pending invites
        if (!workspace.getWorkSpaceOwnerId().getId().equals(requester.getId())) {
            throw new CollaborationExceptions.UnauthorizedException("Only the workspace owner can view invitations.");
        }

        return invitationRepository.findAllByWorkspaceId(workspaceId)
                .stream()
                .map(invite -> InvitationResponseDTO.builder()
                        .id(invite.getId())
                        .email(invite.getEmail())
                        .workspaceName(workspace.getWorkSpaceName())
                        .expiryDate(invite.getExpiryDate())
                        .expired(invite.getExpiryDate().isBefore(LocalDateTime.now()))
                        .build())
                .toList();
    }
}

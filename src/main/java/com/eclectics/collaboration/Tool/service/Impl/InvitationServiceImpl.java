package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.model.Invitation;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.InvitationRepository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class InvitationServiceImpl implements InvitationService {

    private InvitationRepository invitationRepository;
    private WorkSpaceReposiroty workSpaceRepository;

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
}

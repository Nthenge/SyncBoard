package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.model.Invitation;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.InvitationRepository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private WorkSpaceReposiroty workSpaceRepository;

    @Transactional
    @Override
    public void acceptInvite(String token, User invitee) {
        Invitation invite = invitationRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or non-existent invitation token."));

        if (invite.getExpiryDate().isBefore(LocalDateTime.now())) {
            invitationRepository.delete(invite);
            throw new RuntimeException("This invitation has expired.");
        }

        if (!invite.getEmail().equalsIgnoreCase(invitee.getEmail())) {
            throw new RuntimeException("This invite was sent to a different email address.");
        }

        WorkSpace ws = invite.getWorkspace();
        ws.getMembers().add(invitee);

        workSpaceRepository.save(ws);
        invitationRepository.delete(invite);
    }
}

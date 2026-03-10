package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.InvitationResponseDTO;
import com.eclectics.collaboration.Tool.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvitationService {
    void acceptInvite(String token, User invitee);
    void rejectInvite(String token, User invitee);
    void deleteInvitation(Long invitationId, User requester);
    List<InvitationResponseDTO> getWorkspaceInvitations(Long workspaceId, User requester);
}

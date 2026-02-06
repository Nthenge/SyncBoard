package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface InvitationService {
    @Transactional
    void acceptInvite(String token, User invitee);
}

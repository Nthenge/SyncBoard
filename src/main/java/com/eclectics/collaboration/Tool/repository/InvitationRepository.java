package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByInviteToken(String token);
    List<Invitation> findAllByWorkspaceId(Long workspaceId);
}

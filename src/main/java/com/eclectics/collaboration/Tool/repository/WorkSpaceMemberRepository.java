package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.enums.WorkspaceRole;
import com.eclectics.collaboration.Tool.model.WorkSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WorkSpaceMemberRepository extends JpaRepository<WorkSpaceMember, Long> {
    Optional<WorkSpaceMember> findByWorkspace_IdAndUser_Id(Long workspaceId, Long userId);
    boolean existsByWorkspace_IdAndUser_Id(Long workspaceId, Long userId);
    void deleteByWorkspace_IdAndUser_Id(Long workspaceId, Long userId);
    long countByWorkspace_IdAndRole(Long workspaceId, WorkspaceRole role);
}

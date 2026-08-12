package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.StarredWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StarredWorkspaceRepository extends JpaRepository<StarredWorkspace, Long> {
    boolean existsByWorkspace_IdAndUser_Id(Long workspaceId, Long userId);
    Optional<StarredWorkspace> findByWorkspace_IdAndUser_Id(Long workspaceId, Long userId);
    List<StarredWorkspace> findByUser_Id(Long userId);
    void deleteByWorkspace_IdAndUser_Id(Long workspaceId, Long userId);
}

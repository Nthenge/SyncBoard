package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.StarredBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StarredBoardRepository extends JpaRepository<StarredBoard, Long> {
    boolean existsByBoard_IdAndUser_Id(Long boardId, Long userId);
    Optional<StarredBoard> findByBoard_IdAndUser_Id(Long boardId, Long userId);
    List<StarredBoard> findByUser_Id(Long userId);
    void deleteByBoard_IdAndUser_Id(Long boardId, Long userId);
    void deleteByBoard_Id(Long boardId);
    @Modifying
    @Query("DELETE FROM StarredBoard sb WHERE sb.user.id = :userId AND sb.board.workSpaceId.id = :workspaceId")
    void deleteByUserIdAndWorkspaceId(@Param("userId") Long userId, @Param("workspaceId") Long workspaceId);
}
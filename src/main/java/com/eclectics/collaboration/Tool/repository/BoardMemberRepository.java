package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.BoardMember;
import com.eclectics.collaboration.Tool.enums.BoardRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    Optional<BoardMember> findByBoardIdAndUserId(Long boardId, Long userId);

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    List<BoardMember> findByBoardId(Long boardId);

    long countByBoardIdAndRole(Long boardId, BoardRole role);

    List<BoardMember> findAllByUserId(Long userId);

    void deleteByBoardId(Long boardId); // unambiguous single-level — matches existing findByBoardId convention

    @Modifying
    @Query("DELETE FROM BoardMember bm WHERE bm.user.id = :userId AND bm.board.workSpaceId.id = :workspaceId")
    void deleteByUserIdAndWorkspaceId(@Param("userId") Long userId, @Param("workspaceId") Long workspaceId);
}


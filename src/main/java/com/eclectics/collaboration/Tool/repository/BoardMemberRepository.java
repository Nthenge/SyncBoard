package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.BoardMember;
import com.eclectics.collaboration.Tool.model.BoardRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    Optional<BoardMember> findByBoardIdAndUserId(Long boardId, Long userId);

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    List<BoardMember> findByBoardId(Long boardId);

    long countByBoardIdAndRole(Long boardId, BoardRole role);
}


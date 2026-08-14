package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.dto.RecentBoardResponseDTO;
import com.eclectics.collaboration.Tool.model.UserRecentBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRecentBoardRepository extends JpaRepository<UserRecentBoard, Long> {

    @Query("""
        SELECT urb FROM UserRecentBoard urb
        WHERE urb.user.id = :userId
        AND urb.board.id = :boardId
        AND (:listId IS NULL AND urb.list IS NULL OR urb.list.id = :listId)
        AND (:cardId IS NULL AND urb.card IS NULL OR urb.card.id = :cardId)
    """)
    Optional<UserRecentBoard> findByUserBoardListCard(
            @Param("userId") Long userId,
            @Param("boardId") Long boardId,
            @Param("listId") Long listId,
            @Param("cardId") Long cardId
    );

    @Query("""
        SELECT new com.eclectics.collaboration.Tool.dto.RecentBoardResponseDTO(
            b.id,
            b.boardName,
            w.id,
            w.workSpaceName,
            l.id,
            l.title,
            c.id,
            c.title,
            urb.lastAccessedAt
        )
        FROM UserRecentBoard urb
        JOIN urb.board b
        JOIN b.workSpaceId w
        LEFT JOIN urb.list l
        LEFT JOIN urb.card c
        WHERE urb.user.id = :userId
        ORDER BY urb.lastAccessedAt DESC
    """)
    List<RecentBoardResponseDTO> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
    void deleteByCardId(Long cardId);
}
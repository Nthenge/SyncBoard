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

    Optional<UserRecentBoard> findByUserIdAndBoardId(Long userId, Long boardId);

    @Query("""
        SELECT new com.eclectics.collaboration.Tool.dto.RecentBoardResponseDTO(
            b.id,
            b.boardName,
            w.id,
            w.workSpaceName,
            urb.lastAccessedAt
        )
        FROM UserRecentBoard urb
        JOIN urb.board b
        JOIN b.workSpaceId w
        WHERE urb.user.id = :userId
        ORDER BY urb.lastAccessedAt DESC
    """)
    List<RecentBoardResponseDTO> findRecentBoardsByUserId(@Param("userId") Long userId, Pageable pageable);
}

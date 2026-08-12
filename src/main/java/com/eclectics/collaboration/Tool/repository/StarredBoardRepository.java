package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.StarredBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StarredBoardRepository extends JpaRepository<StarredBoard, Long> {
    boolean existsByBoard_IdAndUser_Id(Long boardId, Long userId);
    Optional<StarredBoard> findByBoard_IdAndUser_Id(Long boardId, Long userId);
    List<StarredBoard> findByUser_Id(Long userId);
    void deleteByBoard_IdAndUser_Id(Long boardId, Long userId);
}
package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListEntityRepository extends JpaRepository<ListEntity, Long> {
    List<ListEntity> findByBoardIdOrderByPosition(Long boardId);
}

package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.BoardRole;
import com.eclectics.collaboration.Tool.model.Boards;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardsRepository extends JpaRepository<Boards,Long> {
    List<Boards> findAllByWorkSpaceId_Id(Long workSpaceId);
}

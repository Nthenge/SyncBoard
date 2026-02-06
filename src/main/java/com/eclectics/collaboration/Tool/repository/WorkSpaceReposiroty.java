package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkSpaceReposiroty extends JpaRepository<WorkSpace, Long> {
    List<WorkSpace> findAllByWorkSpaceOwnerId(User user);
}

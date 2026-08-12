package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkSpaceReposiroty extends JpaRepository<WorkSpace, Long> {
    List<WorkSpace> findAllByWorkSpaceOwnerId(User user);
    @Query("SELECT DISTINCT w FROM WorkSpace w LEFT JOIN w.members m " +
            "WHERE w.workSpaceOwnerId = :user OR m = :user")
    List<WorkSpace> findAllByOwnerOrMember(@Param("user") User user);
}

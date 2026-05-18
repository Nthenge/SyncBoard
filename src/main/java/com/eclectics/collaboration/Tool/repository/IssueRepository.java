package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}

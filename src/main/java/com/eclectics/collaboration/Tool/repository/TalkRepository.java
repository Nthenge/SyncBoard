package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.Talk;
import com.eclectics.collaboration.Tool.enums.TalkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkRepository extends JpaRepository<Talk, Long> {
    List<Talk> findByStatus(TalkStatus status);
    List<Talk> findByIssueId(Long issueId);
    List<Talk> findByEmail(String email);
}

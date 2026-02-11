package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.ActivityLogResponseDTO;
import com.eclectics.collaboration.Tool.mapper.ActivityLogMapper;
import com.eclectics.collaboration.Tool.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;

    @GetMapping
    public List<ActivityLogResponseDTO> getBoardActivity(@PathVariable Long boardId) {
        return activityLogRepository
                .findByBoardIdOrderByCreatedAtDesc(boardId)
                .stream()
                .map(ActivityLogMapper::toDto)
                .toList();
    }
}


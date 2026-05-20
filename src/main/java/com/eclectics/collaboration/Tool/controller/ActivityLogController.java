package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.ActivityLogResponseDTO;
import com.eclectics.collaboration.Tool.repository.ActivityLogRepository;
import com.eclectics.collaboration.Tool.mapper.ActivityLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/activity-logs")
@RequiredArgsConstructor
@Tag(name = "Board Activity Logs", description = "Operations for viewing board audit logs and activities")
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;

    @Operation(summary = "Get activity logs for a specific board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board activity logs retrieved successfully")
    })
    @GetMapping
    public List<ActivityLogResponseDTO> getBoardActivity(@PathVariable Long boardId) {
        return activityLogRepository
                .findByBoardIdOrderByCreatedAtDesc(boardId)
                .stream()
                .map(ActivityLogMapper::toDto)
                .toList();
    }
}
package com.eclectics.collaboration.Tool.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityLogResponseDTO {

    private Long id;
    private Long boardId;
    private Long userId;
    private String actionType;
    private String details;
    private LocalDateTime createdAt;
}


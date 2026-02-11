package com.eclectics.collaboration.Tool.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {

    private Long id;
    private String type;
    private String content;
    private boolean read;
    private LocalDateTime createdAt;

}

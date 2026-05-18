package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQResponseDTO {
    private Long id;
    private String question;
    private String answer;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

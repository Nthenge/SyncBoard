package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.enums.TalkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String message;
    private IssueResponseDTO issue;
    private TalkStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

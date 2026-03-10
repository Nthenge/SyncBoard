package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponseDTO {
    private Long id;
    private String email;
    private String workspaceName;
    private LocalDateTime expiryDate;
    private boolean expired;
}

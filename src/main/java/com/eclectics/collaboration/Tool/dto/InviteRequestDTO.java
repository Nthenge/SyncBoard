package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.enums.WorkspaceRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InviteRequestDTO {

    @NotEmpty(message = "Invitations list cannot be empty")
    @Valid
    private List<InviteeDTO> invitations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteeDTO {
        @NotBlank(message = "Email is required")
        private String email;
        private WorkspaceRole role = WorkspaceRole.MEMBER;
    }
}
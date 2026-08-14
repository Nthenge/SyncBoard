package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.enums.WorkspaceRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSpaceMemberDTO {
    private Long id;
    private String firstName;
    private String sirName;
    private String email;
    private String avatarUrl;
    private WorkspaceRole role;
}
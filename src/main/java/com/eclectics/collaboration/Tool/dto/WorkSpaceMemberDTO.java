package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

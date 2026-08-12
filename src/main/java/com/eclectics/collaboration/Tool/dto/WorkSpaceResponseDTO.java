package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSpaceResponseDTO {

    private Long id;
    private String workSpaceName;
    private String workSpaceDescription;
    private LocalDateTime workSpaceCreatedAt;
    private LocalDateTime updatedAt;
    private WorkSpaceMemberDTO owner;
    private boolean isStarred;
    private Set<WorkSpaceMemberDTO> members = new HashSet<>();
}

package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkSpaceResponseDTO {

    private Long id;
    private String workSpaceName;
    private String workSpaceDescription;
    private String workSpaceCreatedBy;
    private LocalDateTime workSpaceCreatedAt;
    private Set<User> members = new HashSet<>();

}

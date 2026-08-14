package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.WorkSpaceMemberDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.model.WorkSpaceMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface WorkSpaceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workSpaceName", source = "dto.workSpaceName")
    @Mapping(target = "workSpaceDescription", source = "dto.workSpaceDescription")
    @Mapping(target = "workSpaceOwnerId", source = "user")
    @Mapping(target = "workSpaceCreatedBy", expression = "java(user.getFirstName() + \" \" + user.getSirName())")
    @Mapping(target = "workSpaceCreatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WorkSpace toEntity(WorkSpaceRequestDTO dto, User user);

    default WorkSpaceResponseDTO toDto(WorkSpace ws) {
        return WorkSpaceResponseDTO.builder()
                .id(ws.getId())
                .workSpaceName(ws.getWorkSpaceName())
                .workSpaceDescription(ws.getWorkSpaceDescription())
                .workSpaceCreatedAt(ws.getWorkSpaceCreatedAt())
                .updatedAt(ws.getUpdatedAt())
                .owner(toMemberDTO(ws.getWorkSpaceOwnerId(), com.eclectics.collaboration.Tool.enums.WorkspaceRole.ADMIN))
                .members(ws.getMembers().stream()
                        .map(this::toMemberDTO)
                        .collect(Collectors.toSet()))
                .build();
    }

    default WorkSpaceMemberDTO toMemberDTO(User user, com.eclectics.collaboration.Tool.enums.WorkspaceRole role) {
        return WorkSpaceMemberDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .sirName(user.getSirName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(role)
                .build();
    }

    default WorkSpaceMemberDTO toMemberDTO(WorkSpaceMember member) {
        return toMemberDTO(member.getUser(), member.getRole());
    }
}
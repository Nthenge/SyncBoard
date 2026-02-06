package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkSpaceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workSpaceName", source = "dto.workSpaceName")
    @Mapping(target = "workSpaceDescription", source = "dto.workSpaceDescription")
    @Mapping(target = "workSpaceOwnerId", source = "user")
    @Mapping(target = "workSpaceCreatedBy", expression = "java(user.getFullName())")
    @Mapping(target = "workSpaceCreatedAt", expression = "java(java.time.LocalDateTime.now())")
    WorkSpace toEntity(WorkSpaceRequestDTO dto, User user);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "workSpaceOwnerId", source = "user")
    WorkSpace toEntity(WorkSpaceResponseDTO dto, User user);
    WorkSpaceResponseDTO toDto(WorkSpace entity);
}
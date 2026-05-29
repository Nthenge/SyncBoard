package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.BoardsRequestDTO;
import com.eclectics.collaboration.Tool.dto.BoardsResponseDTO;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workSpaceId", source = "workSpace")
    @Mapping(target = "boardCreatedBy", source = "user.fullName")
    @Mapping(target = "boardCreatedAt", expression = "java(java.time.LocalDateTime.now())")
    Boards toEntity(BoardsRequestDTO dto, WorkSpace workSpace, User user);

    @Mapping(target = "workSpaceId", source = "workSpaceId.id")  // ← flatten entity to Long
    BoardsResponseDTO toDto(Boards board);
}

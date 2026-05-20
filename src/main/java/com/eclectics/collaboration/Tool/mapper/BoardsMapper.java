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
    // Map the ID field inside the WorkSpace object directly to the Long field in Boards
    @Mapping(target = "workSpaceId", source = "workSpace.id")
    @Mapping(target = "boardCreatedBy", source = "user.fullName")
    @Mapping(target = "boardCreatedAt", expression = "java(java.time.LocalDateTime.now())")
    Boards toEntity(BoardsRequestDTO dto, WorkSpace workSpace, User user);

    BoardsResponseDTO toDto(Boards board);
}

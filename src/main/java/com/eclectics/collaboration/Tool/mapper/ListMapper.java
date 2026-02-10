package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.ListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ListMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "board", source = "board")
    ListEntity toEntity(ListRequestDTO dto, Boards board);

    @Mapping(target = "boardId", source = "board.id")
    ListResponseDTO toDto(ListEntity entity);

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "position", ignore = true)
    ListEntity updateEntityFromDto(ListRequestDTO dto, ListEntity list);
}


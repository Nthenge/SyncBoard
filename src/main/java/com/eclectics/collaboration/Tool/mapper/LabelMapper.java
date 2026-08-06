package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.LabelRequestDTO;
import com.eclectics.collaboration.Tool.dto.LabelResponseDTO;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "board", source = "board")
    Label toEntity(LabelRequestDTO dto, Boards board);

    @Mapping(target = "boardId", source = "board.id")
    LabelResponseDTO toDto(Label entity);
}
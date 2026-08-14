package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import com.eclectics.collaboration.Tool.model.Card;
import com.eclectics.collaboration.Tool.model.ListEntity;
import com.eclectics.collaboration.Tool.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "title", source = "dto.title")
    @Mapping(target = "list", source = "list")
    @Mapping(target = "createdBy", source = "user")
    Card toEntity(CardRequestDTO dto, ListEntity list, User user);

    @Mapping(target = "listId", source = "list.id")
    @Mapping(target = "createdById", source = "createdBy.id")
    CardResponseDTO toDto(Card entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "list", ignore = true)
    void updateEntityFromDto(CardRequestDTO dto, @MappingTarget Card card);
}
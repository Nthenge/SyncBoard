package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.CardRequestDTO;
import com.eclectics.collaboration.Tool.dto.CardResponseDTO;
import com.eclectics.collaboration.Tool.model.Card;
import com.eclectics.collaboration.Tool.model.ListEntity;
import com.eclectics.collaboration.Tool.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "list", source = "list")
    @Mapping(target = "createdBy", source = "user")
    Card toEntity(CardRequestDTO dto, ListEntity list, User user);

    @Mapping(target = "listId", source = "list.id")
    @Mapping(target = "createdById", source = "createdBy.id")
    CardResponseDTO toDto(Card entity);

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    Card updateEntityFromDto(CardRequestDTO dto, Card card);
}


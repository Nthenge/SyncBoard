package com.eclectics.collaboration.Tool.mapper;


import com.eclectics.collaboration.Tool.dto.BoardMemberResponseDTO;
import com.eclectics.collaboration.Tool.model.BoardMember;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardMemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "board", source = "board")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "role", constant = "MEMBER")
    BoardMember toEntity(User user, Boards board);

    @Mapping(target = "boardId", source = "board.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(entity.getUser().getFullName())")
    BoardMemberResponseDTO toDto(BoardMember entity);
}


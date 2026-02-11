package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.CommentResponseDTO;
import com.eclectics.collaboration.Tool.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", expression = "java(comment.getUser().getFullName())")
    CommentResponseDTO toDto(Comment comment);
}


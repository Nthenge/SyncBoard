package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {
    private String content;
    private List<Long> mentionedUserIds;
}
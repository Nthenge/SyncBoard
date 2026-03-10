package com.eclectics.collaboration.Tool.dto;

import lombok.Data;

@Data
public class CardMoveRequestDTO {
    private Long cardId;
    private Long targetListId;
    private Integer newIndex;
}

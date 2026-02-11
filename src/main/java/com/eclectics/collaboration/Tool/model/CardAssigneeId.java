package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class CardAssigneeId implements Serializable {

    private Long cardId;
    private Long userId;

}

package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.model.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime dueDate;
    private Integer position;
    private Long listId;
    private Long createdById;
    private Integer version;
    private LocalDateTime createdAt;

}

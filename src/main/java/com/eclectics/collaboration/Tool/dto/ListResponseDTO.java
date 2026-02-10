package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListResponseDTO {

    private Long id;
    private String title;
    private Integer position;
    private Long boardId;
    private LocalDateTime createdAt;

}


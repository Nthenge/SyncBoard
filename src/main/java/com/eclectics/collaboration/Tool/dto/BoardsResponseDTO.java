package com.eclectics.collaboration.Tool.dto;

import java.time.LocalDateTime;

public class BoardsResponseDTO {

    private Long id;
    private String boardName;
    private String boardDescription;
    private String boardCreatedBy;
    private LocalDateTime boardCreatedAt;
    private Long workSpaceId;

}

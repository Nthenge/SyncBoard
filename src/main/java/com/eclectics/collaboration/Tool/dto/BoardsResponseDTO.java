package com.eclectics.collaboration.Tool.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BoardsResponseDTO {
    private Long id;
    private String boardName;
    private String boardDescription;
    private String boardCreatedBy;
    private LocalDateTime boardCreatedAt;
    private Long workSpaceId;
    @JsonProperty("isStarred")
    private boolean isStarred;
}
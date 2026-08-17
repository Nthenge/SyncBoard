package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private Long cardId;
    private Long authorId;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private List<MentionDTO> mentions;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MentionDTO {
        private Long userId;
        private String userFullName;
    }
}
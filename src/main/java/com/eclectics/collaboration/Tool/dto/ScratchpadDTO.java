package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScratchpadDTO {
    private String content;
    private LocalDateTime updatedAt;
}

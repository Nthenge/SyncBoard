package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.enums.Priority;

import java.time.LocalDateTime;

public record AssignedCardResponseDTO(
        Long cardId,
        String cardTitle,
        Priority priority,
        LocalDateTime dueDate,
        Long listId,
        String listName,
        Long boardId,
        String boardName,
        Long workspaceId,
        String workspaceName
) {}
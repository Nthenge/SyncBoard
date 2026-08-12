package com.eclectics.collaboration.Tool.dto;

import java.time.LocalDateTime;

public record RecentBoardResponseDTO(
        Long id,
        String name,
        Long workspaceId,
        String workspaceName,
        Long listId,
        String listName,
        Long cardId,
        String cardTitle,
        LocalDateTime lastAccessedAt
) {}

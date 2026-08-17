package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.model.Notification;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long id,
        Notification.Type type,
        String message,
        String referenceType,
        Long referenceId,
        boolean read,
        LocalDateTime createdAt
) {}

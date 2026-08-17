package com.eclectics.collaboration.Tool.dto;

public record UpdateNotificationPreferenceRequest(
        boolean emailOnAssign, boolean inAppOnAssign,
        boolean emailOnBoardAdd, boolean inAppOnBoardAdd,
        boolean emailOnMention, boolean inAppOnMention,
        boolean emailOnDueSoon, boolean inAppOnDueSoon
) {}

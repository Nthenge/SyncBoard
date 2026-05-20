package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.NotificationResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.NotificationMapper;
import com.eclectics.collaboration.Tool.model.Notification;
import com.eclectics.collaboration.Tool.repository.NotificationRepository;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Operations for retrieving and managing user notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @Operation(summary = "Get unread notifications for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    @GetMapping
    public List<NotificationResponseDTO> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return notificationRepository
                .findByUserIdAndReadFalse(principal.getId())
                .stream()
                .map(NotificationMapper::toDto)
                .toList();
    }

    @Operation(summary = "Mark a specific notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - notification belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PatchMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(principal.getId())) {
            throw new CollaborationExceptions.ForbiddenException("Not your notification");
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }
}
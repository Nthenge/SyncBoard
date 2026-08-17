package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.NotificationPreferenceResponse;
import com.eclectics.collaboration.Tool.dto.NotificationResponse;
import com.eclectics.collaboration.Tool.dto.UpdateNotificationPreferenceRequest;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notifications and per-user notification preferences")
public class NotificationController {

    private final NotificationService notificationService;
    private final HttpServletRequest request;

    @Operation(summary = "Get the current user's notification preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification preferences fetched successfully")
    })
    @GetMapping("/notification-preferences")
    public ResponseEntity<Object> getPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        NotificationPreferenceResponse preferences =
                notificationService.getPreferences(userDetails.getId());
        return ResponseHandler.generateResponse(
                "Notification preferences fetched successfully",
                HttpStatus.OK,
                preferences,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Update the current user's notification preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification preferences updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PutMapping("/notification-preferences")
    public ResponseEntity<Object> updatePreferences(
            @RequestBody UpdateNotificationPreferenceRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        NotificationPreferenceResponse updated =
                notificationService.updatePreferences(userDetails.getId(), dto);
        return ResponseHandler.generateResponse(
                "Notification preferences updated successfully",
                HttpStatus.OK,
                updated,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Get the current user's notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications fetched successfully")
    })
    @GetMapping("/notifications")
    public ResponseEntity<Object> getNotifications(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<NotificationResponse> notifications =
                notificationService.getNotifications(userDetails.getId(), pageable);
        return ResponseHandler.generateResponse(
                "Notifications fetched successfully",
                HttpStatus.OK,
                notifications,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Mark a notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Object> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.markRead(id, userDetails.getId());
        return ResponseHandler.generateResponse(
                "Notification marked as read",
                HttpStatus.OK,
                null,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Get the current user's unread notification count")
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Object> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        long count = notificationService.getUnreadCount(userDetails.getId());
        return ResponseHandler.generateResponse(
                "Unread notification count fetched successfully",
                HttpStatus.OK,
                count,
                request.getRequestURI()
        );
    }

    @Operation(summary = "Mark all of the current user's notifications as read")
    @PatchMapping("/notifications/read-all")
    public ResponseEntity<Object> markAllRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.markAllRead(userDetails.getId());
        return ResponseHandler.generateResponse(
                "All notifications marked as read",
                HttpStatus.OK,
                null,
                request.getRequestURI()
        );
    }
}
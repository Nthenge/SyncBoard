package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.NotificationResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.NotificationMapper;
import com.eclectics.collaboration.Tool.model.Notification;
import com.eclectics.collaboration.Tool.repository.NotificationRepository;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

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


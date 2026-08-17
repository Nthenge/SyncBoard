package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.NotificationPreferenceResponse;
import com.eclectics.collaboration.Tool.dto.NotificationResponse;
import com.eclectics.collaboration.Tool.dto.UpdateNotificationPreferenceRequest;
import com.eclectics.collaboration.Tool.enums.NotificationType;
import com.eclectics.collaboration.Tool.model.Notification;
import com.eclectics.collaboration.Tool.model.NotificationPreference;
import com.eclectics.collaboration.Tool.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationService {

    @Transactional(readOnly = true)
    NotificationPreferenceResponse getPreferences(Long userId);

    @Transactional
    NotificationPreferenceResponse updatePreferences(Long userId, UpdateNotificationPreferenceRequest req);

    @Transactional
    NotificationPreference getOrCreatePreferenceEntity(Long userId);

    @Transactional
    Notification create(User recipient, Notification.Type type, String message,
                        String referenceType, Long referenceId);

    @Transactional(readOnly = true)
    Page<NotificationResponse> getNotifications(Long userId, Pageable pageable);

    @Transactional
    void markRead(Long notificationId, Long requestingUserId);

    @Transactional(readOnly = true)
    long getUnreadCount(Long userId);

    @Transactional
    void markAllRead(Long userId);
}

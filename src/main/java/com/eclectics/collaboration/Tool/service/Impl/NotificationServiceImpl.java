package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.model.Notification;
import com.eclectics.collaboration.Tool.model.NotificationType;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.NotificationRepository;
import com.eclectics.collaboration.Tool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public void notify(User user, NotificationType type, String content) {
        Notification notification = new Notification(
                null,
                user,
                type,
                content,
                false,
                LocalDateTime.now()
        );
        notificationRepository.save(notification);
    }

}

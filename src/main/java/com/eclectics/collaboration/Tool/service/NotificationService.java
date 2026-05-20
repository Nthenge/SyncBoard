package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.enums.NotificationType;
import com.eclectics.collaboration.Tool.model.User;

public interface NotificationService {
    public void notify(User user, NotificationType type, String content);
}

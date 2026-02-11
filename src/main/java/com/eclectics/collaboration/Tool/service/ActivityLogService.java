package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.model.ActionType;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;

public interface ActivityLogService {
    public void log(
            Boards board,
            User actor,
            ActionType actionType,
            String details
    );
}

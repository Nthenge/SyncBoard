package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.model.ActionType;
import com.eclectics.collaboration.Tool.model.ActivityLog;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.ActivityLogRepository;
import com.eclectics.collaboration.Tool.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Override
    public void log(
            Boards board,
            User actor,
            ActionType actionType,
            String details
    ) {
        ActivityLog log = new ActivityLog(
                null,
                board,
                actor,
                actionType,
                details,
                LocalDateTime.now()
        );
        activityLogRepository.save(log);
    }

}

package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import org.springframework.transaction.annotation.Transactional;

public interface WorkSpaceService {
    WorkSpace createWorkspace(User user, WorkSpaceRequestDTO request);

    @Transactional
    void deleteWorkspace(Long workspaceId, User user);
}

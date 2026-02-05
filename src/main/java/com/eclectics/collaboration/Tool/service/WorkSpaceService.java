package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;

public interface WorkSpaceService {
    WorkSpace createWorkspace(User user, WorkSpaceRequestDTO request);
}

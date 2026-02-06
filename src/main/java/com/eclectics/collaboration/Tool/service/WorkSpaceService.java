package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WorkSpaceService {
    WorkSpace createWorkspace(User user, WorkSpaceRequestDTO request);

    List<WorkSpaceResponseDTO> myWorkspaces();

    @Transactional
    void deleteWorkspace(Long workspaceId, User user);
}

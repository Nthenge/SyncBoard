package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.mapper.WorkSpaceMapper;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private final WorkSpaceReposiroty workSpaceReposiroty;
    private final WorkSpaceMapper workSpaceMapper;

    @Override
    public WorkSpace createWorkspace(User user, WorkSpaceRequestDTO request) {
        WorkSpace ws = workSpaceMapper.toEntity(request, user);
        return workSpaceReposiroty.save(ws);
    }

    @Transactional
    @Override
    public void deleteWorkspace(Long workspaceId, User user) {
        WorkSpace ws = workSpaceReposiroty.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        if (!ws.getWorkSpaceOwnerId().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this workspace");
        }
        workSpaceReposiroty.delete(ws);
    }
}

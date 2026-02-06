package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.WorkSpaceMapper;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceReposiroty workSpaceReposiroty;
    private final WorkSpaceMapper workSpaceMapper;
    private final UserRespository userRespository;

    @Override
    public WorkSpace createWorkspace(User user, WorkSpaceRequestDTO request) {
        WorkSpace ws = workSpaceMapper.toEntity(request, user);
        return workSpaceReposiroty.save(ws);
    }

    @Override
    public List<WorkSpaceResponseDTO> myWorkspaces() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = (principal instanceof UserDetails) ?
                ((UserDetails)principal).getUsername() : principal.toString();

        User user = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        List<WorkSpace> workspaces = workSpaceReposiroty.findAllByWorkSpaceOwnerId(user);

        return workspaces.stream()
                .map(workSpaceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteWorkspace(Long workspaceId, User user) {
        WorkSpace ws = workSpaceReposiroty.findById(workspaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));

        if (!ws.getWorkSpaceOwnerId().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this workspace");
        }
        workSpaceReposiroty.delete(ws);
    }
}

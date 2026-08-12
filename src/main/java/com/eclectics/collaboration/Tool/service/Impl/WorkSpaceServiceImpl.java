package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.WorkSpaceMapper;
import com.eclectics.collaboration.Tool.model.StarredWorkspace;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.repository.StarredWorkspaceRepository;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.repository.WorkSpaceReposiroty;
import com.eclectics.collaboration.Tool.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    private final WorkSpaceReposiroty workSpaceReposiroty;
    private final WorkSpaceMapper workSpaceMapper;
    private final UserRespository userRespository;
    private final StarredWorkspaceRepository starredWorkspaceRepository;

    @Override
    public WorkSpaceResponseDTO createWorkspace(User user, WorkSpaceRequestDTO request) {
        WorkSpace ws = workSpaceMapper.toEntity(request, user);
        WorkSpace saved = workSpaceReposiroty.save(ws);
        log.info("Workspace created id={} by user={}", saved.getId(), user.getEmail());
        return workSpaceMapper.toDto(saved);
    }

    @Override
    public List<WorkSpaceResponseDTO> myWorkspaces() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = (principal instanceof UserDetails) ?
                ((UserDetails)principal).getUsername() : principal.toString();

        User user = userRespository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        List<WorkSpace> workspaces = workSpaceReposiroty.findAllByOwnerOrMember(user);

        Set<Long> starredIds = starredWorkspaceRepository.findByUser_Id(user.getId()).stream()
                .map(sw -> sw.getWorkspace().getId())
                .collect(Collectors.toSet());

        return workspaces.stream()
                .map(ws -> {
                    WorkSpaceResponseDTO dto = workSpaceMapper.toDto(ws);
                    dto.setStarred(starredIds.contains(ws.getId()));
                    return dto;
                })
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

    @Transactional
    @Override
    public boolean toggleStarWorkspace(Long workspaceId, Long userId) {
        WorkSpace workspace = workSpaceReposiroty.findById(workspaceId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Workspace not found"));
        User user = userRespository.findById(userId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        Optional<StarredWorkspace> existing = starredWorkspaceRepository.findByWorkspace_IdAndUser_Id(workspaceId, userId);
        if (existing.isPresent()) {
            starredWorkspaceRepository.delete(existing.get());
            return false;
        } else {
            starredWorkspaceRepository.save(new StarredWorkspace(workspace, user));
            return true;
        }
    }

    @Override
    public List<WorkSpaceResponseDTO> getStarredWorkspaces(Long userId) {
        return starredWorkspaceRepository.findByUser_Id(userId).stream()
                .map(sw -> {
                    WorkSpaceResponseDTO dto = workSpaceMapper.toDto(sw.getWorkspace());
                    dto.setStarred(true);
                    return dto;
                })
                .toList();
    }
}

package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.IssueRequestDTO;
import com.eclectics.collaboration.Tool.dto.IssueResponseDTO;
import com.eclectics.collaboration.Tool.model.Issue;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public Issue toEntity(IssueRequestDTO dto) {
        return Issue.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.isActive())
                .build();
    }

    public IssueResponseDTO toResponse(Issue issue) {
        return IssueResponseDTO.builder()
                .id(issue.getId())
                .name(issue.getName())
                .description(issue.getDescription())
                .active(issue.isActive())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(IssueRequestDTO dto, Issue existing) {
        if (dto.getName() != null)        existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        existing.setActive(dto.isActive());
    }
}

package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.TalkRequestDTO;
import com.eclectics.collaboration.Tool.dto.TalkResponseDTO;
import com.eclectics.collaboration.Tool.model.Issue;
import com.eclectics.collaboration.Tool.model.Talk;
import com.eclectics.collaboration.Tool.model.TalkStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TalkMapper {

    private final IssueMapper issueMapper;

    public Talk toEntity(TalkRequestDTO dto, Issue issue) {
        return Talk.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .issue(issue)
                .status(TalkStatus.PENDING)
                .build();
    }

    public TalkResponseDTO toResponse(Talk talk) {
        return TalkResponseDTO.builder()
                .id(talk.getId())
                .fullName(talk.getFullName())
                .email(talk.getEmail())
                .message(talk.getMessage())
                .issue(issueMapper.toResponse(talk.getIssue()))
                .status(talk.getStatus())
                .createdAt(talk.getCreatedAt())
                .updatedAt(talk.getUpdatedAt())
                .build();
    }
}

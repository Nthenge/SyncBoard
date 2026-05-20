package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.TalkRequestDTO;
import com.eclectics.collaboration.Tool.dto.TalkResponseDTO;
import com.eclectics.collaboration.Tool.dto.TalkStatusUpdateDTO;
import com.eclectics.collaboration.Tool.enums.TalkStatus;

import java.util.List;

public interface TalkService {
    TalkResponseDTO submitTalk(TalkRequestDTO requestDTO);
    TalkResponseDTO getTalkById(Long id);
    List<TalkResponseDTO> getAllTalks();
    List<TalkResponseDTO> getTalksByStatus(TalkStatus status);
    List<TalkResponseDTO> getTalksByIssue(Long issueId);
    List<TalkResponseDTO> getTalksByEmail(String email);
    TalkResponseDTO updateStatus(Long id, TalkStatusUpdateDTO statusUpdateDTO);
    void deleteTalk(Long id);
}

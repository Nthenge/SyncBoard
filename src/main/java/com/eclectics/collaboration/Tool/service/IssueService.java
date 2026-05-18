package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.IssueRequestDTO;
import com.eclectics.collaboration.Tool.dto.IssueResponseDTO;

import java.util.List;

public interface IssueService {
    IssueResponseDTO createIssue(IssueRequestDTO requestDTO);
    IssueResponseDTO getIssueById(Long id);
    List<IssueResponseDTO> getAllIssues();
    List<IssueResponseDTO> getActiveIssues();
    IssueResponseDTO updateIssue(Long id, IssueRequestDTO requestDTO);
    void deleteIssue(Long id);
    IssueResponseDTO toggleActive(Long id);
}

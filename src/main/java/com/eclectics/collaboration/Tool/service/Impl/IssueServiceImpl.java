package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.IssueRequestDTO;
import com.eclectics.collaboration.Tool.dto.IssueResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.IssueMapper;
import com.eclectics.collaboration.Tool.model.Issue;
import com.eclectics.collaboration.Tool.repository.IssueRepository;
import com.eclectics.collaboration.Tool.service.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final IssueMapper issueMapper;

    @Override
    public IssueResponseDTO createIssue(IssueRequestDTO requestDTO) {
        if (issueRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            throw new CollaborationExceptions.ResourceAlreadyExistsException(
                    "An issue category with this name already exists");
        }
        Issue saved = issueRepository.save(issueMapper.toEntity(requestDTO));
        log.info("Issue created id={}", saved.getId());
        return issueMapper.toResponse(saved);
    }

    @Override
    public IssueResponseDTO getIssueById(Long id) {
        return issueMapper.toResponse(findOrThrow(id));
    }

    @Override
    public List<IssueResponseDTO> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(issueMapper::toResponse)
                .toList();
    }

    @Override
    public List<IssueResponseDTO> getActiveIssues() {
        return issueRepository.findByActiveTrue().stream()
                .map(issueMapper::toResponse)
                .toList();
    }

    @Override
    public IssueResponseDTO updateIssue(Long id, IssueRequestDTO requestDTO) {
        Issue existing = findOrThrow(id);

        boolean duplicateName = issueRepository.existsByNameIgnoreCase(requestDTO.getName())
                && !existing.getName().equalsIgnoreCase(requestDTO.getName());

        if (duplicateName) {
            throw new CollaborationExceptions.ResourceAlreadyExistsException(
                    "Another issue category with this name already exists");
        }

        issueMapper.updateEntityFromDTO(requestDTO, existing);
        log.info("Issue updated id={}", id);
        return issueMapper.toResponse(issueRepository.save(existing));
    }

    @Override
    public void deleteIssue(Long id) {
        issueRepository.delete(findOrThrow(id));
        log.info("Issue deleted id={}", id);
    }

    @Override
    public IssueResponseDTO toggleActive(Long id) {
        Issue issue = findOrThrow(id);
        issue.setActive(!issue.isActive());
        log.info("Issue id={} active toggled to {}", id, issue.isActive());
        return issueMapper.toResponse(issueRepository.save(issue));
    }

    private Issue findOrThrow(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "Issue not found with id: " + id));
    }
}

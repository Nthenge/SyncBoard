package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.TalkRequestDTO;
import com.eclectics.collaboration.Tool.dto.TalkResponseDTO;
import com.eclectics.collaboration.Tool.dto.TalkStatusUpdateDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.TalkMapper;
import com.eclectics.collaboration.Tool.model.Issue;
import com.eclectics.collaboration.Tool.model.Talk;
import com.eclectics.collaboration.Tool.model.TalkStatus;
import com.eclectics.collaboration.Tool.repository.IssueRepository;
import com.eclectics.collaboration.Tool.repository.TalkRepository;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.TalkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalkServiceImpl implements TalkService {

    private final TalkRepository talkRepository;
    private final IssueRepository issueRepository;
    private final TalkMapper talkMapper;
    private final EmailService emailService;

    @Override
    public TalkResponseDTO submitTalk(TalkRequestDTO requestDTO) {
        Issue issue = issueRepository.findById(requestDTO.getIssueId())
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "Issue not found with id: " + requestDTO.getIssueId()));

        if (!issue.isActive()) {
            throw new CollaborationExceptions.BadRequestException(
                    "The selected issue category is no longer available");
        }

        Talk saved = talkRepository.save(talkMapper.toEntity(requestDTO, issue));
        log.info("Talk submitted id={} for issue={}", saved.getId(), issue.getName());

        try {
            emailService.sendNewSupportNotification(
                    saved.getFullName(),
                    saved.getEmail(),
                    issue.getName(),
                    saved.getMessage()
            );
        } catch (Exception e) {
            log.error("Failed to send admin support notification for talk id={} — reason: {}", saved.getId(), e.getMessage());
        }

        return talkMapper.toResponse(saved);
    }

    @Override
    public TalkResponseDTO getTalkById(Long id) {
        return talkMapper.toResponse(findOrThrow(id));
    }

    @Override
    public List<TalkResponseDTO> getAllTalks() {
        return talkRepository.findAll().stream()
                .map(talkMapper::toResponse)
                .toList();
    }

    @Override
    public List<TalkResponseDTO> getTalksByStatus(TalkStatus status) {
        return talkRepository.findByStatus(status).stream()
                .map(talkMapper::toResponse)
                .toList();
    }

    @Override
    public List<TalkResponseDTO> getTalksByIssue(Long issueId) {
        return talkRepository.findByIssueId(issueId).stream()
                .map(talkMapper::toResponse)
                .toList();
    }

    @Override
    public List<TalkResponseDTO> getTalksByEmail(String email) {
        return talkRepository.findByEmail(email).stream()
                .map(talkMapper::toResponse)
                .toList();
    }

    @Override
    public TalkResponseDTO updateStatus(Long id, TalkStatusUpdateDTO statusUpdateDTO) {
        Talk talk = findOrThrow(id);
        talk.setStatus(statusUpdateDTO.getStatus());
        log.info("Talk id={} status updated to {}", id, statusUpdateDTO.getStatus());
        return talkMapper.toResponse(talkRepository.save(talk));
    }

    @Override
    public void deleteTalk(Long id) {
        talkRepository.delete(findOrThrow(id));
        log.info("Talk deleted id={}", id);
    }

    private Talk findOrThrow(Long id) {
        return talkRepository.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "Talk not found with id: " + id));
    }
}

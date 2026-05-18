package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.FAQRequestDTO;
import com.eclectics.collaboration.Tool.dto.FAQResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.FAQMapper;
import com.eclectics.collaboration.Tool.model.FAQs;
import com.eclectics.collaboration.Tool.repository.FAQRepository;
import com.eclectics.collaboration.Tool.service.FAQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FAQServiceImpl implements FAQService {

    private final FAQRepository faqRepository;
    private final FAQMapper faqMapper;

    @Override
    public FAQResponseDTO createFAQ(FAQRequestDTO requestDTO) {
        if (faqRepository.existsByQuestionIgnoreCase(requestDTO.getQuestion())) {
            throw new CollaborationExceptions.ResourceAlreadyExistsException(
                    "An FAQ with this question already exists");
        }
        FAQs faq = faqMapper.toEntity(requestDTO);
        FAQs saved = faqRepository.save(faq);
        log.info("FAQ created with id={}", saved.getId());
        return faqMapper.toResponse(saved);
    }

    @Override
    public FAQResponseDTO getFAQById(Long id) {
        FAQs faq = faqRepository.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "FAQ not found with id: " + id));
        return faqMapper.toResponse(faq);
    }

    @Override
    public List<FAQResponseDTO> getAllFAQs() {
        return faqRepository.findAll()
                .stream()
                .map(faqMapper::toResponse)
                .toList();
    }

    @Override
    public List<FAQResponseDTO> getActiveFAQs() {
        return faqRepository.findByActiveTrue()
                .stream()
                .map(faqMapper::toResponse)
                .toList();
    }

    @Override
    public FAQResponseDTO updateFAQ(Long id, FAQRequestDTO requestDTO) {
        FAQs existing = faqRepository.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "FAQ not found with id: " + id));

        boolean duplicateExists = faqRepository.existsByQuestionIgnoreCase(requestDTO.getQuestion())
                && !existing.getQuestion().equalsIgnoreCase(requestDTO.getQuestion());

        if (duplicateExists) {
            throw new CollaborationExceptions.ResourceAlreadyExistsException(
                    "Another FAQ with this question already exists");
        }

        faqMapper.updateEntityFromDTO(requestDTO, existing);
        FAQs updated = faqRepository.save(existing);
        log.info("FAQ updated id={}", updated.getId());
        return faqMapper.toResponse(updated);
    }

    @Override
    public void deleteFAQ(Long id) {
        FAQs faq = faqRepository.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "FAQ not found with id: " + id));
        faqRepository.delete(faq);
        log.info("FAQ deleted id={}", id);
    }

    @Override
    public FAQResponseDTO toggleActive(Long id) {
        FAQs faq = faqRepository.findById(id)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "FAQ not found with id: " + id));
        faq.setActive(!faq.isActive());
        FAQs updated = faqRepository.save(faq);
        log.info("FAQ id={} active toggled to {}", id, updated.isActive());
        return faqMapper.toResponse(updated);
    }
}

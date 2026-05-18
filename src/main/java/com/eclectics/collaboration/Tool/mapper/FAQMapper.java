package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.FAQRequestDTO;
import com.eclectics.collaboration.Tool.dto.FAQResponseDTO;
import com.eclectics.collaboration.Tool.model.FAQs;
import org.springframework.stereotype.Component;

@Component
public class FAQMapper {

    public FAQs toEntity(FAQRequestDTO dto) {
        return FAQs.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .active(dto.isActive())
                .build();
    }

    public FAQResponseDTO toResponse(FAQs faq) {
        return FAQResponseDTO.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .active(faq.isActive())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(FAQRequestDTO dto, FAQs existingFaq) {
        if (dto.getQuestion() != null) existingFaq.setQuestion(dto.getQuestion());
        if (dto.getAnswer() != null)   existingFaq.setAnswer(dto.getAnswer());
        existingFaq.setActive(dto.isActive());
    }
}

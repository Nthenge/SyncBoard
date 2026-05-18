package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.FAQRequestDTO;
import com.eclectics.collaboration.Tool.dto.FAQResponseDTO;

import java.util.List;

public interface FAQService {
    FAQResponseDTO createFAQ(FAQRequestDTO requestDTO);
    FAQResponseDTO getFAQById(Long id);
    List<FAQResponseDTO> getAllFAQs();
    List<FAQResponseDTO> getActiveFAQs();
    FAQResponseDTO updateFAQ(Long id, FAQRequestDTO requestDTO);
    void deleteFAQ(Long id);
    FAQResponseDTO toggleActive(Long id);
}

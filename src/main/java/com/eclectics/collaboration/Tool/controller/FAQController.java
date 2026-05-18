package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.FAQRequestDTO;
import com.eclectics.collaboration.Tool.dto.FAQResponseDTO;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.FAQService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/faqs")
@RequiredArgsConstructor
public class FAQController {

    private final FAQService faqService;
    private final HttpServletRequest request;

    @PostMapping
    public ResponseEntity<Object> createFAQ(@RequestBody @Valid FAQRequestDTO requestDTO) {
        FAQResponseDTO response = faqService.createFAQ(requestDTO);
        return ResponseHandler.generateResponse("FAQ created successfully", HttpStatus.CREATED, response, request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFAQById(@PathVariable Long id) {
        FAQResponseDTO response = faqService.getFAQById(id);
        return ResponseHandler.generateResponse("FAQ fetched", HttpStatus.OK, response, request.getRequestURI());
    }

    @GetMapping
    public ResponseEntity<Object> getAllFAQs() {
        List<FAQResponseDTO> response = faqService.getAllFAQs();
        return ResponseHandler.generateResponse("FAQs fetched", HttpStatus.OK, response, request.getRequestURI());
    }

    @GetMapping("/active")
    public ResponseEntity<Object> getActiveFAQs() {
        List<FAQResponseDTO> response = faqService.getActiveFAQs();
        return ResponseHandler.generateResponse("Active FAQs fetched", HttpStatus.OK, response, request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateFAQ(@PathVariable Long id, @RequestBody @Valid FAQRequestDTO requestDTO) {
        FAQResponseDTO response = faqService.updateFAQ(id, requestDTO);
        return ResponseHandler.generateResponse("FAQ updated successfully", HttpStatus.OK, response, request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseHandler.generateResponse("FAQ deleted successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @PatchMapping("/{id}/activate-deactivate")
    public ResponseEntity<Object> toggleActive(@PathVariable Long id) {
        FAQResponseDTO response = faqService.toggleActive(id);
        return ResponseHandler.generateResponse("FAQ visibility toggled", HttpStatus.OK, response, request.getRequestURI());
    }
}

package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.FAQRequestDTO;
import com.eclectics.collaboration.Tool.dto.FAQResponseDTO;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "FAQs", description = "CRUD and status operations for Frequently Asked Questions")
public class FAQController {

    private final FAQService faqService;
    private final HttpServletRequest request;

    @Operation(summary = "Create a new FAQ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "FAQ created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<Object> createFAQ(@RequestBody @Valid FAQRequestDTO requestDTO) {
        FAQResponseDTO response = faqService.createFAQ(requestDTO);
        return ResponseHandler.generateResponse("FAQ created successfully", HttpStatus.CREATED, response, request.getRequestURI());
    }

    @Operation(summary = "Get FAQ by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ fetched"),
            @ApiResponse(responseCode = "404", description = "FAQ not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getFAQById(@PathVariable Long id) {
        FAQResponseDTO response = faqService.getFAQById(id);
        return ResponseHandler.generateResponse("FAQ fetched", HttpStatus.OK, response, request.getRequestURI());
    }

    @Operation(summary = "Get all FAQs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQs fetched")
    })
    @GetMapping
    public ResponseEntity<Object> getAllFAQs() {
        List<FAQResponseDTO> response = faqService.getAllFAQs();
        return ResponseHandler.generateResponse("FAQs fetched", HttpStatus.OK, response, request.getRequestURI());
    }

    @Operation(summary = "Get all active FAQs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active FAQs fetched")
    })
    @GetMapping("/active")
    public ResponseEntity<Object> getActiveFAQs() {
        List<FAQResponseDTO> response = faqService.getActiveFAQs();
        return ResponseHandler.generateResponse("Active FAQs fetched", HttpStatus.OK, response, request.getRequestURI());
    }

    @Operation(summary = "Update an existing FAQ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ updated successfully"),
            @ApiResponse(responseCode = "404", description = "FAQ not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateFAQ(@PathVariable Long id, @RequestBody @Valid FAQRequestDTO requestDTO) {
        FAQResponseDTO response = faqService.updateFAQ(id, requestDTO);
        return ResponseHandler.generateResponse("FAQ updated successfully", HttpStatus.OK, response, request.getRequestURI());
    }

    @Operation(summary = "Delete an FAQ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ deleted successfully"),
            @ApiResponse(responseCode = "404", description = "FAQ not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseHandler.generateResponse("FAQ deleted successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @Operation(summary = "Toggle FAQ active status (activate/deactivate)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ visibility toggled"),
            @ApiResponse(responseCode = "404", description = "FAQ not found")
    })
    @PatchMapping("/{id}/activate-deactivate")
    public ResponseEntity<Object> toggleActive(@PathVariable Long id) {
        FAQResponseDTO response = faqService.toggleActive(id);
        return ResponseHandler.generateResponse("FAQ visibility toggled", HttpStatus.OK, response, request.getRequestURI());
    }
}
package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.IssueRequestDTO;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.IssueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final HttpServletRequest request;

    @PostMapping
    public ResponseEntity<Object> createIssue(@RequestBody @Valid IssueRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Issue created", HttpStatus.CREATED,
                issueService.createIssue(requestDTO), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return ResponseHandler.generateResponse("Issue fetched", HttpStatus.OK,
                issueService.getIssueById(id), request.getRequestURI());
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return ResponseHandler.generateResponse("Issues fetched", HttpStatus.OK,
                issueService.getAllIssues(), request.getRequestURI());
    }

    // Public-facing — dropdown options when user fills the talk-to-us form
    @GetMapping("/active")
    public ResponseEntity<Object> getActive() {
        return ResponseHandler.generateResponse("Active issues fetched", HttpStatus.OK,
                issueService.getActiveIssues(), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody @Valid IssueRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Issue updated", HttpStatus.OK,
                issueService.updateIssue(id, requestDTO), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseHandler.generateResponse("Issue deleted", HttpStatus.OK,
                null, request.getRequestURI());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Object> toggle(@PathVariable Long id) {
        return ResponseHandler.generateResponse("Issue visibility toggled", HttpStatus.OK,
                issueService.toggleActive(id), request.getRequestURI());
    }
}

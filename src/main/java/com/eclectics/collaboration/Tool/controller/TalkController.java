package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.TalkRequestDTO;
import com.eclectics.collaboration.Tool.dto.TalkStatusUpdateDTO;
import com.eclectics.collaboration.Tool.enums.TalkStatus;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.TalkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
public class TalkController {

    private final TalkService talkService;
    private final HttpServletRequest request;

    // Public — no auth required, anyone can submit
    @PostMapping
    public ResponseEntity<Object> submit(@RequestBody @Valid TalkRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Message submitted successfully", HttpStatus.CREATED,
                talkService.submitTalk(requestDTO), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return ResponseHandler.generateResponse("Talk fetched", HttpStatus.OK,
                talkService.getTalkById(id), request.getRequestURI());
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getAllTalks(), request.getRequestURI());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> getByStatus(@PathVariable TalkStatus status) {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getTalksByStatus(status), request.getRequestURI());
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<Object> getByIssue(@PathVariable Long issueId) {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getTalksByIssue(issueId), request.getRequestURI());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> getByEmail(@PathVariable String email) {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getTalksByEmail(email), request.getRequestURI());
    }

    // Admin — update ticket status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateStatus(@PathVariable Long id,
                                               @RequestBody @Valid TalkStatusUpdateDTO statusDTO) {
        return ResponseHandler.generateResponse("Status updated", HttpStatus.OK,
                talkService.updateStatus(id, statusDTO), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        talkService.deleteTalk(id);
        return ResponseHandler.generateResponse("Talk deleted", HttpStatus.OK,
                null, request.getRequestURI());
    }
}

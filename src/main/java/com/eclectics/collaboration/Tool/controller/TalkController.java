package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.TalkRequestDTO;
import com.eclectics.collaboration.Tool.dto.TalkStatusUpdateDTO;
import com.eclectics.collaboration.Tool.enums.TalkStatus;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.TalkService;
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

@Slf4j
@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
@Tag(name = "Talks", description = "Operations for user queries, support requests, and contact messages")
public class TalkController {

    private final TalkService talkService;
    private final HttpServletRequest request;

    @Operation(summary = "Submit a new contact or support message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping
    public ResponseEntity<Object> submit(@RequestBody @Valid TalkRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Message submitted successfully", HttpStatus.CREATED,
                talkService.submitTalk(requestDTO), request.getRequestURI());
    }

    @Operation(summary = "Get a talk record by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Talk fetched"),
            @ApiResponse(responseCode = "404", description = "Talk not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return ResponseHandler.generateResponse("Talk fetched", HttpStatus.OK,
                talkService.getTalkById(id), request.getRequestURI());
    }

    @Operation(summary = "Get all talk records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Talks fetched")
    })
    @GetMapping
    public ResponseEntity<Object> getAll() {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getAllTalks(), request.getRequestURI());
    }

    @Operation(summary = "Get talk records filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Talks fetched")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<Object> getByStatus(@PathVariable TalkStatus status) {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getTalksByStatus(status), request.getRequestURI());
    }

    @Operation(summary = "Get talk records filtered by issue type ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Talks fetched")
    })
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<Object> getByIssue(@PathVariable Long issueId) {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getTalksByIssue(issueId), request.getRequestURI());
    }

    @Operation(summary = "Get talk records filtered by submitter email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Talks fetched")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Object> getByEmail(@PathVariable String email) {
        return ResponseHandler.generateResponse("Talks fetched", HttpStatus.OK,
                talkService.getTalksByEmail(email), request.getRequestURI());
    }

    @Operation(summary = "Update the processing status of a talk record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "404", description = "Talk not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateStatus(@PathVariable Long id,
                                               @RequestBody @Valid TalkStatusUpdateDTO statusDTO) {
        return ResponseHandler.generateResponse("Status updated", HttpStatus.OK,
                talkService.updateStatus(id, statusDTO), request.getRequestURI());
    }

    @Operation(summary = "Delete a talk record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Talk deleted"),
            @ApiResponse(responseCode = "404", description = "Talk not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        talkService.deleteTalk(id);
        return ResponseHandler.generateResponse("Talk deleted", HttpStatus.OK,
                null, request.getRequestURI());
    }
}
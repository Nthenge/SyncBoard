package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.IssueRequestDTO;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.IssueService;
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
@RequestMapping("/issues")
@RequiredArgsConstructor
@Tag(name = "Issues", description = "CRUD and visibility operations for support/ticket issues")
public class IssueController {

    private final IssueService issueService;
    private final HttpServletRequest request;

    @Operation(summary = "Create a new Issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Issue created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<Object> createIssue(@RequestBody @Valid IssueRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Issue created", HttpStatus.CREATED,
                issueService.createIssue(requestDTO), request.getRequestURI());
    }

    @Operation(summary = "Get Issue by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue fetched"),
            @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return ResponseHandler.generateResponse("Issue fetched", HttpStatus.OK,
                issueService.getIssueById(id), request.getRequestURI());
    }

    @Operation(summary = "Get all Issues")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issues fetched")
    })
    @GetMapping
    public ResponseEntity<Object> getAll() {
        return ResponseHandler.generateResponse("Issues fetched", HttpStatus.OK,
                issueService.getAllIssues(), request.getRequestURI());
    }

    @Operation(summary = "Get all active Issues")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active issues fetched")
    })
    @GetMapping("/active")
    public ResponseEntity<Object> getActive() {
        return ResponseHandler.generateResponse("Active issues fetched", HttpStatus.OK,
                issueService.getActiveIssues(), request.getRequestURI());
    }

    @Operation(summary = "Update an existing Issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue updated"),
            @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody @Valid IssueRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Issue updated", HttpStatus.OK,
                issueService.updateIssue(id, requestDTO), request.getRequestURI());
    }

    @Operation(summary = "Delete an Issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue deleted"),
            @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseHandler.generateResponse("Issue deleted", HttpStatus.OK,
                null, request.getRequestURI());
    }

    @Operation(summary = "Toggle Issue active status (activate/deactivate)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue visibility toggled"),
            @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Object> toggle(@PathVariable Long id) {
        return ResponseHandler.generateResponse("Issue visibility toggled", HttpStatus.OK,
                issueService.toggleActive(id), request.getRequestURI());
    }
}
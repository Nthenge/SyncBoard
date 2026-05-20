package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.SystemConfigRequestDTO;
import com.eclectics.collaboration.Tool.enums.ConfigKey;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.SystemConfigService;
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
@RequestMapping("/system-config")
@RequiredArgsConstructor
@Tag(name = "System Configuration", description = "Operations for managing global system configuration parameters")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final HttpServletRequest request;

    @Operation(summary = "Create a new system configuration parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Config created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid SystemConfigRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Config created", HttpStatus.CREATED,
                systemConfigService.create(requestDTO), request.getRequestURI());
    }

    @Operation(summary = "Update an existing system configuration value by its key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Config updated"),
            @ApiResponse(responseCode = "404", description = "Configuration key not found")
    })
    @PutMapping("/{configKey}")
    public ResponseEntity<Object> update(@PathVariable ConfigKey configKey,
                                         @RequestBody @Valid SystemConfigRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Config updated", HttpStatus.OK,
                systemConfigService.update(configKey, requestDTO), request.getRequestURI());
    }

    @Operation(summary = "Get a system configuration parameter by its unique key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Config fetched"),
            @ApiResponse(responseCode = "404", description = "Configuration key not found")
    })
    @GetMapping("/{configKey}")
    public ResponseEntity<Object> getByKey(@PathVariable ConfigKey configKey) {
        return ResponseHandler.generateResponse("Config fetched", HttpStatus.OK,
                systemConfigService.getByKey(configKey), request.getRequestURI());
    }

    @Operation(summary = "Get all global system configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configs fetched")
    })
    @GetMapping
    public ResponseEntity<Object> getAll() {
        return ResponseHandler.generateResponse("Configs fetched", HttpStatus.OK,
                systemConfigService.getAll(), request.getRequestURI());
    }
}
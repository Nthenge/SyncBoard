package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.SystemConfigRequestDTO;
import com.eclectics.collaboration.Tool.model.ConfigKey;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.SystemConfigService;
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
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final HttpServletRequest request;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid SystemConfigRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Config created", HttpStatus.CREATED,
                systemConfigService.create(requestDTO), request.getRequestURI());
    }

    @PutMapping("/{configKey}")
    public ResponseEntity<Object> update(@PathVariable ConfigKey configKey,
                                         @RequestBody @Valid SystemConfigRequestDTO requestDTO) {
        return ResponseHandler.generateResponse("Config updated", HttpStatus.OK,
                systemConfigService.update(configKey, requestDTO), request.getRequestURI());
    }

    @GetMapping("/{configKey}")
    public ResponseEntity<Object> getByKey(@PathVariable ConfigKey configKey) {
        return ResponseHandler.generateResponse("Config fetched", HttpStatus.OK,
                systemConfigService.getByKey(configKey), request.getRequestURI());
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return ResponseHandler.generateResponse("Configs fetched", HttpStatus.OK,
                systemConfigService.getAll(), request.getRequestURI());
    }
}

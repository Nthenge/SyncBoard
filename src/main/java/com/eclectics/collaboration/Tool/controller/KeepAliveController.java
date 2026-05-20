package com.eclectics.collaboration.Tool.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Tag(name = "System Monitoring", description = "Public infrastructure and service health check endpoints")
public class KeepAliveController {

    @Operation(summary = "Check application service status", description = "Returns the simple uptime status of the application cluster instance. Commonly checked by load balancers or keep-alive pings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application server instance running normally")
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UP");
    }
}
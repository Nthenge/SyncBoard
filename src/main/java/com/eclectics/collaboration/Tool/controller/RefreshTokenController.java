package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.AuthResponse;
import com.eclectics.collaboration.Tool.dto.RefreshTokenRequest;
import com.eclectics.collaboration.Tool.model.RefreshToken;
import com.eclectics.collaboration.Tool.security.JwtUtil;
import com.eclectics.collaboration.Tool.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {

        String requestToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found."));

        // Validates expiry — deletes and throws if expired
        refreshTokenService.verifyExpiration(refreshToken);

        // Issue a new access token
        String email = refreshToken.getUser().getEmail();
        String newAccessToken = jwtUtil.generateToken(email);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, requestToken));
    }
}

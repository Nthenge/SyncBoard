package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshResponseDTO {
    private String token;
    private String refreshToken;
}

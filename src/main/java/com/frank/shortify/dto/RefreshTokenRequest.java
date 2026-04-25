package com.frank.shortify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "The refresh token is required")
    private String refreshToken;
}

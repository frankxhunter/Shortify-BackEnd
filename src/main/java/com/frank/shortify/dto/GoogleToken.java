package com.frank.shortify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleToken {
    @NotBlank(message = "The token is required")
    private String token;
}

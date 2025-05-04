package com.frank.shortiy.shortify.dto;

import com.frank.shortiy.shortify.Validators.annotations.SecurePassword;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank( message = "The email is required")
    @Size(min = 8, max = 20, message = "Email must be between 8 and 20 characters")
    @Email
    private String email;

    @NotBlank( message = "The password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @SecurePassword
    private String password;
}

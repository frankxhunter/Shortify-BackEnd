package com.frank.shortify.dto;

import com.frank.shortify.Validators.annotations.SecurePassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "The email is required")
    @Size(min = 8, max = 20, message = "Email must be between 8 and 20 characters")
    @Email
    private String email;

    @NotBlank(message = "The password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @SecurePassword
    private String password;
}

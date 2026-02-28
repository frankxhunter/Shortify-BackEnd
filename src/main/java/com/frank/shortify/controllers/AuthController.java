package com.frank.shortify.controllers;

import com.frank.shortify.dto.GoogleToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/auth")
public interface AuthController {
    @PostMapping("/google")
    ResponseEntity<?> googleLogin(
            @Valid
            @RequestBody
            GoogleToken token,
            HttpServletRequest request
    ) throws Exception;

    @GetMapping("/confirm-email")
    ResponseEntity<?> confirmEmail(@RequestParam("token") @NotBlank(message = "Token is required") String token);

    @PostMapping("/logout")
    ResponseEntity<?> logout(HttpServletRequest request);
}

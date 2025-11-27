package com.frank.shortify.controllers;

import com.frank.shortify.dto.GoogleToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}

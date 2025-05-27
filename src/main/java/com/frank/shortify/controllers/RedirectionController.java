package com.frank.shortify.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping()
public interface RedirectionController {

    @GetMapping("/{hash}")
    ResponseEntity<Object> redirection(
            @NotBlank(message = "The hash of URL is required")
            @PathVariable String hash,
            HttpServletRequest request);
}

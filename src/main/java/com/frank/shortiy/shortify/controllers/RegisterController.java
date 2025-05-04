package com.frank.shortiy.shortify.controllers;

import com.frank.shortiy.shortify.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@RequestMapping("/register")
@RestController
public interface RegisterController {

    @PostMapping
    public ResponseEntity<?> register(
            @RequestBody @Valid UserDto userDto
    );

    @GetMapping
    public ResponseEntity<?> checkLogin(Principal principal);
}

package com.frank.shortify.controllers;

import com.frank.shortify.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@RequestMapping
@RestController
public interface RegisterController {

    @PostMapping("/register")
    ResponseEntity<?> register(
            @RequestBody @Valid UserDto userDto,
            HttpServletRequest request
    );

    @PostMapping("/login")
    ResponseEntity<?> logIn(
            @RequestBody @Valid UserDto userDto,
            HttpServletRequest request
    );

    @GetMapping("/login")
    ResponseEntity<?> checkLogin(Principal principal);
}

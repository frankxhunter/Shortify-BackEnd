package com.frank.shortiy.shortify.controllers;

import com.frank.shortiy.shortify.Validators.annotations.UrlFormat;
import com.frank.shortiy.shortify.models.Url;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Validated
@RequestMapping("/urls")
public interface UrlController {

    @GetMapping
    Iterable<Url> getAllUrls(Principal principal);

    @GetMapping("/{id}")
    Url findUrlById(
            Principal pricipal,
            @PathVariable
            @NotNull(message = "The url id is required")
            Long id
    );

    @PostMapping("/create")
    ResponseEntity<Url> createUrl(
            @Valid
            @NotBlank(message = "The url is required")
            @UrlFormat String url,
            Principal principal
    );

}

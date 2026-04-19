package com.frank.shortify.controllers;

import com.frank.shortify.Validators.annotations.UrlFormat;
import com.frank.shortify.dto.CreateUrlDto;
import com.frank.shortify.models.Url;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@RequestMapping("/api/urls")
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
            @RequestBody CreateUrlDto url,
            Principal principal
    );

    @PutMapping("/{id}")
    Url updateUrl(
            @Valid
            @NotBlank(message = "The new url is required")
            @UrlFormat String url,
            @PathVariable
            @NotNull Long id,
            Principal principal
    );

}

package com.frank.shortiy.shortify.controllers;

import com.frank.shortiy.shortify.models.InfoRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/urls/{id}/requests")
public interface InfoRequestController {

    @GetMapping()
    Iterable<InfoRequest> findByUrl(
            Principal principal,
            @PathVariable
            @NotNull(message = "The id is required")
            Long id
    );
}

package com.frank.shortiy.shortify.controllers.impl;

import com.frank.shortiy.shortify.controllers.UrlController;
import com.frank.shortiy.shortify.exceptions.ResourceNotFoundException;
import com.frank.shortiy.shortify.models.Url;
import com.frank.shortiy.shortify.models.User;
import com.frank.shortiy.shortify.services.UrlService;
import com.frank.shortiy.shortify.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UrlControllerImpl implements UrlController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UserService userService;

    @Override
    public Iterable<Url> getAllUrls(Principal principal) {
        return urlService.getAll(
                userService.findByEmail(principal.getName()).orElse(null)
        );
    }

    @Override
    public Url findUrlById(Principal principal, Long id) {
        return urlService.findUrl(principal.getName(), id).orElseThrow(() -> new ResourceNotFoundException(
                "Not found the url with id: " + id
        ));

    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<Url> createUrl(String url, Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByEmail(principal.getName()).orElse(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(urlService.saveUrl(url, user));
    }
}

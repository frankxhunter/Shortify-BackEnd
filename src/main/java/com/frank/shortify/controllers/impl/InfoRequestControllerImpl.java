package com.frank.shortify.controllers.impl;

import com.frank.shortify.controllers.InfoRequestController;
import com.frank.shortify.exceptions.ResourceNotFoundException;
import com.frank.shortify.models.InfoRequest;
import com.frank.shortify.services.InfoRequestService;
import com.frank.shortify.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class InfoRequestControllerImpl implements InfoRequestController {

    @Autowired
    private InfoRequestService infoRequestService;
    @Autowired
    private UrlService urlService;

    @Override
    public Iterable<InfoRequest> findByUrl(Principal principal, Long id) {
        return urlService.findUrl(principal.getName(), id)
                .map((value) -> infoRequestService.findByUrl(value))
                .orElseThrow(() -> new ResourceNotFoundException
                        ("The url with id: " + id + " is not found"));
    }
}

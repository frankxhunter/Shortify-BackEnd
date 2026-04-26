package com.frank.shortify.controllers.impl;

import com.frank.shortify.controllers.RedirectionController;
import com.frank.shortify.exceptions.ResourceNotFoundException;
import com.frank.shortify.models.InfoRequest;
import com.frank.shortify.models.Url;
import com.frank.shortify.services.ClickCounterWebSocket;
import com.frank.shortify.services.InfoRequestService;
import com.frank.shortify.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RedirectionControllerImpl implements RedirectionController {

    private final UrlService urlService;
    private final InfoRequestService infoRequestService;
    private final ClickCounterWebSocket clickCounterWebSocket;

    @Override
    public ResponseEntity<Object> redirection(String hash, HttpServletRequest request) {
        Optional<Url> originalUrl = urlService.findUrlByShortUrl(hash);
        return originalUrl.map(url -> {
            urlService.incrementClickCounter(url);
            clickCounterWebSocket.sendIncrement(url.getId(), url.getClickCounter());
            InfoRequest infoRequest = infoRequestService.getInfoRequestFromHttpRequest(request);
            infoRequest.setUrl(url);
            infoRequestService.save(infoRequest);

            url.getInfoRequests().size();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, url.getOriginalUrl()).build();
        }).orElseThrow(() -> new ResourceNotFoundException("The url is not found"));
    }

    @Override
    public String checkStatus() {
        return "ok";
    }

    @Override
    public ResponseEntity<Object> redirectToFrontEnd() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "https://app.shortfy.link").build();
    }


}

package com.frank.shortify.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClickCounterWebSocket {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendIncrement(long idUrl, int clickCounter) {
        simpMessagingTemplate.convertAndSend("/topic/url/" + idUrl, clickCounter);
    }
}

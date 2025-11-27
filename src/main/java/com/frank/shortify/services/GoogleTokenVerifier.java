package com.frank.shortify.services;

import com.frank.shortify.exceptions.GoogleUnauthorizedException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${GOOGLE_CLIENT_ID}") String googleClientId) throws Exception {
        verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                new GsonFactory()
        ).setAudience(Collections.singletonList(googleClientId)).build();
    }

    public Payload verify(String token) throws Exception {
        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null || idToken.getPayload() == null) {
            throw new GoogleUnauthorizedException("Invalid Google token");
        }
        return idToken.getPayload();
    }
}

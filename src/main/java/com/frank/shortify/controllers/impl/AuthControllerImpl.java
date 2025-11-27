package com.frank.shortify.controllers.impl;

import com.frank.shortify.Utils.UtilsRequest;
import com.frank.shortify.controllers.AuthController;
import com.frank.shortify.dto.GoogleToken;
import com.frank.shortify.models.Roles;
import com.frank.shortify.models.User;
import com.frank.shortify.services.GoogleTokenVerifier;
import com.frank.shortify.services.UrlService;
import com.frank.shortify.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthControllerImpl implements AuthController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleTokenVerifier googleVerifier;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    record GoogleAuthResponse(String email) {
    }


    @Override
    public ResponseEntity<?> googleLogin(GoogleToken googleToken, HttpServletRequest request) throws Exception {
        Payload payload = googleVerifier.verify(googleToken.getToken());
        String email = payload.getEmail();

        saveUserIfNotExist(email);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Authentication authentication = authenticateUser(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UtilsRequest.setCookieSession(authentication, request);

        return ResponseEntity.ok(new GoogleAuthResponse(email));
    }

    @NotNull
    private static Authentication authenticateUser(UserDetails userDetails) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        return authentication;
    }

    private void saveUserIfNotExist(String email) {
        userService.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword("");
            newUser.setRole(Roles.USER);
            return userService.save(newUser);
        });
    }
}

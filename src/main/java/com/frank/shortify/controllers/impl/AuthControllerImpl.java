package com.frank.shortify.controllers.impl;

import com.frank.shortify.configuration.JwtService;
import com.frank.shortify.controllers.AuthController;
import com.frank.shortify.dto.GoogleToken;
import com.frank.shortify.dto.RefreshTokenRequest;
import com.frank.shortify.dto.UserDto;
import com.frank.shortify.models.Roles;
import com.frank.shortify.models.User;
import com.frank.shortify.services.EmailVerificationService;
import com.frank.shortify.services.GoogleTokenVerifier;
import com.frank.shortify.services.RefreshTokenService;
import com.frank.shortify.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.Optional;


@RestController
public class AuthControllerImpl implements AuthController {


    @Autowired
    private UserService userService;

    @Autowired
    private GoogleTokenVerifier googleVerifier;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    record AuthResponse(
            String token,
            String refreshToken,
            String tokenType,
            long expiresInMs,
            long refreshExpiresInMs,
            String email
    ) {
    }

    @Override
    public ResponseEntity<?> register(UserDto userDto, HttpServletRequest request) {
        Optional<User> foundUser = userService.findByEmail(userDto.getEmail());

        if (foundUser.isEmpty()) {
            User user = userService.convertFromDto(userDto);
            user.setRole(Roles.USER);
            userService.save(user);
            emailVerificationService.createVerificationToken(user, getUriString(request));
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered. Please check your email to confirm your account.");
        } else if (!foundUser.get().isEmailVerified()) {
            User existingUser = foundUser.get();
            userService.update(existingUser, userDto);
            emailVerificationService.deleteTokensForUser(existingUser);
            emailVerificationService.createVerificationToken(existingUser, getUriString(request));
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered. Please check your email to confirm your account.");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exist");
    }

    @NotNull
    private static String getUriString(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }

    @Override
    public ResponseEntity<?> logIn(UserDto userDto, HttpServletRequest request) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return buildAuthResponse((UserDetails) auth.getPrincipal(), user.getEmail(), user);
    }

    @Override
    public ResponseEntity<?> checkLogin(Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok(principal.getName());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
    }

    @Override
    public ResponseEntity<?> googleLogin(GoogleToken googleToken, HttpServletRequest request) throws Exception {
        Payload payload = googleVerifier.verify(googleToken.getToken());
        String email = payload.getEmail();

        saveUserIfNotExist(email);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return buildAuthResponse(userDetails, email, user);
    }

    @Override
    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();
            String email = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            refreshTokenService.getValidRefreshToken(refreshToken, user);

            String accessToken = jwtService.generateAccessToken(userDetails);
            String rotatedRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, user);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .body(new AuthResponse(
                            accessToken,
                            rotatedRefreshToken,
                            "Bearer",
                            jwtService.getAccessExpirationMs(),
                            jwtService.getRefreshExpirationMs(),
                            email
                    ));
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> confirmEmail(String token) {
        try {
            emailVerificationService.confirmEmail(token);
            return ResponseEntity.ok("Email confirmed successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request, RefreshTokenRequest refreshTokenRequest) {
        if (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null && !refreshTokenRequest.getRefreshToken().isBlank()) {
            try {
                refreshTokenService.revokeRefreshToken(refreshTokenRequest.getRefreshToken());
            } catch (IllegalArgumentException ignored) {
                // Logout should remain idempotent even if the refresh token is already invalid.
            }
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successfully. Delete the access token on the client side.");
    }

    private void saveUserIfNotExist(String email) {
        userService.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword("");
            newUser.setRole(Roles.USER);
            newUser.setEmailVerified(true);
            return userService.save(newUser);
        });
    }

    private ResponseEntity<AuthResponse> buildAuthResponse(UserDetails userDetails, String email, User user) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(new AuthResponse(
                        accessToken,
                        refreshToken,
                        "Bearer",
                        jwtService.getAccessExpirationMs(),
                        jwtService.getRefreshExpirationMs(),
                        email
                ));
    }
}

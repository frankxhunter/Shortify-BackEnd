package com.frank.shortify.controllers.impl;

import com.frank.shortify.Utils.UtilsRequest;
import com.frank.shortify.controllers.AuthController;
import com.frank.shortify.dto.GoogleToken;
import com.frank.shortify.dto.UserDto;
import com.frank.shortify.models.Roles;
import com.frank.shortify.models.User;
import com.frank.shortify.services.EmailVerificationService;
import com.frank.shortify.services.GoogleTokenVerifier;
import com.frank.shortify.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;

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

    record GoogleAuthResponse(String email) {
    }

    @Override
    public ResponseEntity<?> register(UserDto userDto, HttpServletRequest request) {
        Optional<User> foundUser = userService.findByEmail(userDto.getEmail());

        if (foundUser.isEmpty()) {
            User user = userService.convertFromDto(userDto);
            user.setRole(Roles.USER);
            userService.save(user);
            emailVerificationService.createVerificationToken(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered. Please check your email to confirm your account.");
        } else if (!foundUser.get().isEmailVerified()) {
            User existingUser = foundUser.get();
            userService.update(existingUser, userDto);
            emailVerificationService.deleteTokensForUser(existingUser);
            emailVerificationService.createVerificationToken(existingUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered. Please check your email to confirm your account.");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exist");
    }

    @Override
    public ResponseEntity<?> logIn(UserDto userDto, HttpServletRequest request) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        UtilsRequest.setCookieSession(auth, request);

        return ResponseEntity.ok("Login Successfully");
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

        Authentication authentication = authenticateUser(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UtilsRequest.setCookieSession(authentication, request);

        return ResponseEntity.ok(new GoogleAuthResponse(email));
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
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successfully");
    }

    @NotNull
    private static Authentication authenticateUser(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return authentication;
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
}

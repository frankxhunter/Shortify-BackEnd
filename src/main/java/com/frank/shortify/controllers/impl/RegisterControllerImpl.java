package com.frank.shortify.controllers.impl;

import com.frank.shortify.Utils.UtilsRequest;
import com.frank.shortify.controllers.RegisterController;
import com.frank.shortify.dto.UserDto;
import com.frank.shortify.models.Roles;
import com.frank.shortify.models.User;
import com.frank.shortify.services.EmailVerificationService;
import com.frank.shortify.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
public class RegisterControllerImpl implements RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Override
    public ResponseEntity<?> register(UserDto userDto, HttpServletRequest request) {
        Optional<User> foundUser = userService.findByEmail(userDto.getEmail());

        if (foundUser.isEmpty()) {
            User user = userService.convertFromDto(userDto);
            user.setRole(Roles.USER);
            userService.save(user);
            emailVerificationService.createVerificationToken(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered. Please check your email to confirm your account.");
        } else if (!foundUser.get().isEmailVerified()) {
            User existingUser = foundUser.get();
            userService.update(existingUser, userDto);
            emailVerificationService.deleteTokensForUser(existingUser);
            emailVerificationService.createVerificationToken(existingUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered. Please check your email to confirm your account.");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exist");
    }

    @Override
    public ResponseEntity<?> logIn(UserDto userDto,
                                   HttpServletRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(),
                        userDto.getPassword())
        );
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
}

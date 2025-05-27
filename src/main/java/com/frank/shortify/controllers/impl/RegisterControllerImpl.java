package com.frank.shortify.controllers.impl;

import com.frank.shortify.Utils.UtilsRequest;
import com.frank.shortify.controllers.RegisterController;
import com.frank.shortify.dto.UserDto;
import com.frank.shortify.models.Roles;
import com.frank.shortify.models.User;
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

@RestController
public class RegisterControllerImpl implements RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public ResponseEntity<?> register(UserDto userDto, HttpServletRequest request) {
        User user = userService.convertFromDto(userDto);
        if (userService.findByEmail(user.getEmail()).isEmpty()) {
            user.setRole(Roles.USER);
            userService.save(user);
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getEmail(),
                            userDto.getPassword())
            );
            UtilsRequest.setCookieSession(auth, request);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exist");
        }
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

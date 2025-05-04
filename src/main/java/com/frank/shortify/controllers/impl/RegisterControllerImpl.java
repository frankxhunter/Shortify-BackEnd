package com.frank.shortiy.shortify.controllers.impl;

import com.frank.shortiy.shortify.controllers.RegisterController;
import com.frank.shortiy.shortify.dto.UserDto;
import com.frank.shortiy.shortify.models.Roles;
import com.frank.shortiy.shortify.models.User;
import com.frank.shortiy.shortify.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class RegisterControllerImpl implements RegisterController {

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<?> register(UserDto userDto) {
        User user = userService.convertFromDto(userDto);
        if (userService.findByEmail(user.getEmail()).isEmpty()) {
            user.setRole(Roles.USER);
            userService.save(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exist");
        }
    }

    @Override
    public ResponseEntity<?> checkLogin(Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok(principal.getName());
        } else {
            return ResponseEntity.ok("");
        }
    }
}

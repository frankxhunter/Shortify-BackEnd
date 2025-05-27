package com.frank.shortify.configuration;

import com.frank.shortify.models.User;
import com.frank.shortify.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserShoritfyDetailsServices implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Notar que aqui el username se va a referir a el email
        User user = userService.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User details not found for the user with email: " + username));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));

        return new org.springframework.security.core.userdetails
                .User(user.getEmail(), user.getPassword(), authorities);
    }

}

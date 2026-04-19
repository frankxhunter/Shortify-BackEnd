package com.frank.shortify.configuration.dev;

import jakarta.servlet.SessionCookieConfig;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class SessionConfig {

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig session = servletContext.getSessionCookieConfig();
            session.setMaxAge(60 * 60 * 24 * 7);
            session.setSecure(false);
            session.setHttpOnly(true);
            session.setPath("/");
        };
    }
}
package com.frank.shortify.configuration.prod;

import jakarta.servlet.SessionCookieConfig;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class SessionConfig {

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig session = servletContext.getSessionCookieConfig();
            session.setMaxAge(60 * 60);
            session.setSecure(true);
            session.setHttpOnly(true);
            session.setPath("/");
        };
    }
}


package com.frank.shortify.services;

import com.frank.shortify.models.EmailVerificationToken;
import com.frank.shortify.models.User;
import com.frank.shortify.repositories.EmailVerificationTokenRepository;
import com.frank.shortify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    public String createVerificationToken(User user, String baseUrl) {
        String tokenValue = generateRandomToken();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUsed(false);

        tokenRepository.save(token);

        emailService.sendEmailConfirmation(user, tokenValue, baseUrl);

        return tokenValue;
    }

    @Transactional
    public void confirmEmail(String tokenValue) {
        EmailVerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expired or already used verification token");
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public void deleteTokensForUser(User user) {
        tokenRepository.deleteByUser(user);
    }
}


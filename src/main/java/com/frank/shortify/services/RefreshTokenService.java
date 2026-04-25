package com.frank.shortify.services;

import com.frank.shortify.configuration.JwtService;
import com.frank.shortify.models.RefreshToken;
import com.frank.shortify.models.User;
import com.frank.shortify.repositories.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtService.getRefreshExpirationMs())));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        return jwtService.generateRefreshToken(user.getEmail(), refreshToken.getTokenId());
    }

    @Transactional
    public void revokeRefreshToken(String refreshTokenValue) {
        String tokenId = jwtService.extractTokenId(refreshTokenValue);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public String rotateRefreshToken(String refreshTokenValue, User user) {
        RefreshToken currentToken = getValidRefreshToken(refreshTokenValue, user);
        currentToken.setRevoked(true);
        refreshTokenRepository.save(currentToken);

        return createRefreshToken(user);
    }

    @Transactional
    public RefreshToken getValidRefreshToken(String refreshTokenValue, User user) {
        String tokenId = jwtService.extractTokenId(refreshTokenValue);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        if (!refreshToken.getUser().getEmail().equals(user.getEmail())) {
            throw new IllegalArgumentException("Refresh token does not belong to the user");
        }

        return refreshToken;
    }

    @Transactional
    public void deleteTokensForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}

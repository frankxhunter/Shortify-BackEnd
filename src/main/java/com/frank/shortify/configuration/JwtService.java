package com.frank.shortify.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final ObjectMapper objectMapper;
    private final byte[] signingKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${application.security.jwt.secret}") String secret,
            @Value("${application.security.jwt.access-expiration-ms:900000}") long accessExpirationMs,
            @Value("${application.security.jwt.refresh-expiration-ms:2592000000}") long refreshExpirationMs
    ) {
        this.objectMapper = objectMapper;
        this.signingKey = secret.getBytes(StandardCharsets.UTF_8);
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessExpirationMs);

        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", userDetails.getUsername());
        payload.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        payload.put("token_type", "access");
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiration.getEpochSecond());

        return buildToken(header, payload);
    }

    public String generateRefreshToken(String username, String tokenId) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(refreshExpirationMs);

        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", username);
        payload.put("token_type", "refresh");
        payload.put("jti", tokenId);
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiration.getEpochSecond());

        return buildToken(header, payload);
    }

    private String buildToken(Map<String, Object> header, Map<String, Object> payload) {
        String encodedHeader = encodeJson(header);
        String encodedPayload = encodeJson(payload);
        String signature = sign(encodedHeader + "." + encodedPayload);

        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public String extractUsername(String token) {
        return getClaims(token).get("sub", String.class);
    }

    public String extractTokenType(String token) {
        return getClaims(token).get("token_type", String.class);
    }

    public String extractTokenId(String token) {
        return getClaims(token).get("jti", String.class);
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, "access");
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, "refresh");
    }

    private boolean isTokenValid(String token, UserDetails userDetails, String expectedType) {
        String username = extractUsername(token);
        String tokenType = extractTokenType(token);
        return userDetails.getUsername().equals(username)
                && expectedType.equals(tokenType)
                && !isTokenExpired(token);
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private JwtClaims getClaims(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature = sign(signingInput);
        if (!expectedSignature.equals(parts[2])) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }

        try {
            Map<String, Object> claims = objectMapper.readValue(
                    URL_DECODER.decode(parts[1]),
                    new TypeReference<>() {
                    }
            );
            return new JwtClaims(claims);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse JWT", ex);
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to serialize JWT content", ex);
        }
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(signingKey, HMAC_ALGORITHM));
            return URL_ENCODER.encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign JWT", ex);
        }
    }

    private static class JwtClaims {
        private final Map<String, Object> claims;

        private JwtClaims(Map<String, Object> claims) {
            this.claims = claims;
        }

        private <T> T get(String key, Class<T> targetType) {
            Object value = claims.get(key);
            if (value == null) {
                return null;
            }
            return targetType.cast(value);
        }

        private Date getExpiration() {
            Number exp = get("exp", Number.class);
            if (exp == null) {
                throw new IllegalArgumentException("JWT without expiration");
            }
            return Date.from(Instant.ofEpochSecond(exp.longValue()));
        }

        @SuppressWarnings("unused")
        private List<String> getRoles() {
            return get("roles", List.class);
        }
    }
}

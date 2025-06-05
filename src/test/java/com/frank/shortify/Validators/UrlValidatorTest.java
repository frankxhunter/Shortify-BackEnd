package com.frank.shortify.Validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlValidatorTest {

    private UrlValidator urlValidator;

    @BeforeEach
    void setup() {
        urlValidator = new UrlValidator();
    }

    @DisplayName("Validates a correct HTTP URL")
    @Test
    void validHttpUrl() {
        Assertions.assertTrue(urlValidator.isValid("http://example.com", null));
    }

    @DisplayName("Validates a correct HTTPS URL")
    @Test
    void validHttpsUrl() {
        Assertions.assertTrue(urlValidator.isValid("https://example.com", null));
    }

    @DisplayName("Validates a correct FTP URL")
    @Test
    void validFtpUrl() {
        Assertions.assertTrue(urlValidator.isValid("ftp://example.com", null));
    }

    @DisplayName("Fails validation for null value")
    @Test
    void nullValue() {
        Assertions.assertFalse(urlValidator.isValid(null, null));
    }

    @DisplayName("Fails validation for empty string")
    @Test
    void emptyString() {
        Assertions.assertFalse(urlValidator.isValid("", null));
    }

    @DisplayName("Fails validation for invalid URL format")
    @Test
    void invalidUrlFormat() {
        Assertions.assertFalse(urlValidator.isValid("invalid-url", null));
    }

    @DisplayName("Fails validation for URL exceeding max length")
    @Test
    void urlExceedingMaxLength() {
        String longUrl = "http://example.com/" + "a".repeat(2050);
        Assertions.assertFalse(urlValidator.isValid(longUrl, null));
    }

    @DisplayName("Validates URL at max length boundary")
    @Test
    void urlAtMaxLengthBoundary() {
        String maxLengthUrl = "http://example.com/" + "a".repeat(2048 - ("http://example.com/").length());
        Assertions.assertTrue(urlValidator.isValid(maxLengthUrl, null));
    }
}
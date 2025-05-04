package com.frank.shortiy.shortify.Validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurePasswordValidatorTest {

    private SecurePasswordValidator securePasswordValidator;

    @BeforeAll
    void setUp() {
        securePasswordValidator = new SecurePasswordValidator();
    }

    @Test
    void isValidTest() {
        Assertions.assertTrue(securePasswordValidator.isValid("Password123!", null));
    }

}
package com.frank.shortify.Validators;

import com.frank.shortify.Validators.annotations.SecurePassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SecurePasswordValidator implements ConstraintValidator<SecurePassword, String> {

    private static final String PASSWORD_PATTERN =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)(?!.*\\s).*$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(PASSWORD_PATTERN);
    }
}

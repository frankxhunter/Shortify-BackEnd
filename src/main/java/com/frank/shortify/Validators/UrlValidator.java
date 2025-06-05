package com.frank.shortify.Validators;

import com.frank.shortify.Validators.annotations.UrlFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<UrlFormat, String> {

    private static final String URL_PATTERN =
            "^(https?|ftp):\\/\\/([a-z0-9-]+\\.)+[a-z0-9]{2,4}(\\/[^\\s]*)?$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(URL_PATTERN) && value.length() <= 2048;
    }
}

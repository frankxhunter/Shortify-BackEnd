package com.frank.shortify.Validators.annotations;

import com.frank.shortify.Validators.UrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlFormat {
    String message() default "Url format is incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

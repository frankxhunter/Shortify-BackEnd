package com.frank.shortiy.shortify.Validators.annotations;

import com.frank.shortiy.shortify.Validators.SecurePasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SecurePasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurePassword {

    String message () default "Password must include upper and lower case letters, a number, and a symbol";
    Class<?>[] groups() default {};
    Class<? extends Payload>[]payload() default{};
}

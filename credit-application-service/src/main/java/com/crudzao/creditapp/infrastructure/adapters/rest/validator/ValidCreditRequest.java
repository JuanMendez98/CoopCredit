package com.crudzao.creditapp.infrastructure.adapters.rest.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validator for cross-field validation in credit requests.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreditRequestValidator.class)
@Documented
public @interface ValidCreditRequest {
    String message() default "Invalid credit request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

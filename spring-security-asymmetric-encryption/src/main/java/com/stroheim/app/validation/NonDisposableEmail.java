package com.stroheim.app.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailDomainValidator.class)
public @interface NonDisposableEmail {

    String message() default "Disposable email addresses are not allowed.";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}

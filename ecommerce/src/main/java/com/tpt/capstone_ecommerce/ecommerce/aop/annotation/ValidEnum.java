package com.tpt.capstone_ecommerce.ecommerce.aop.annotation;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "Invalid value. Allowed values are: {enumClass}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

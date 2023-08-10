package com.klaus.saas.commons.validation.constraints;

import com.klaus.saas.commons.validation.EnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author shilin
 * @since 2020-03-10
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumValid {

	String message() default "枚举值错误";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<?> target() default Class.class;

	boolean ignoreEmpty() default true;

}
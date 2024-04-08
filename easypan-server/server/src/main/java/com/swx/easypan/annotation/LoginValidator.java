package com.swx.easypan.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginValidator {
    boolean validated() default true;

    boolean checkAdmin() default false;
}

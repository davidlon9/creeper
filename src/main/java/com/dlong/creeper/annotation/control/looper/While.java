package com.dlong.creeper.annotation.control.looper;

import com.dlong.creeper.annotation.control.ExecutionMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface While {
    String coniditionExpression() default "true";
    ExecutionMode executionMode() default ExecutionMode.SEQUENTIAL;
}

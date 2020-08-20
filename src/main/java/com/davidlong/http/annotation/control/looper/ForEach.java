package com.davidlong.http.annotation.control.looper;

import com.davidlong.http.annotation.control.ExecutionMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForEach{
    String itemsContextKey();
    String itemName() default "item";
    ExecutionMode executionMode() default ExecutionMode.SEQUENTIAL;
}

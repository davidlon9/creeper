package com.davidlong.creeper.annotation.control.looper;


import com.davidlong.creeper.annotation.control.ExecutionMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForIndex {
    String start();
    String end();
    String indexName() default "index";
    ExecutionMode executionMode() default ExecutionMode.SEQUENTIAL;
}

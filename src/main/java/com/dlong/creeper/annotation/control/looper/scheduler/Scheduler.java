package com.dlong.creeper.annotation.control.looper.scheduler;

import com.dlong.creeper.annotation.control.ExecutionMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduler {
    Trigger trigger();
    ExecutionMode executionMode() default ExecutionMode.SEQUENTIAL;
}

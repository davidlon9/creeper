package com.davidlong.creeper.annotation.seq.multi;

import com.davidlong.creeper.annotation.seq.Request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Request
public @interface MultiRequest {
    int index();
    String name() default "";
    String description() default "";
    int threadSize() default 1;
    int interval() default 100;
    boolean shareContext() default true;
    boolean moveStopAll() default true;
}

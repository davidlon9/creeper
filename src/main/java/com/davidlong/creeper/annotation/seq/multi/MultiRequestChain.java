package com.davidlong.http.annotation.seq.multi;

import com.davidlong.http.annotation.seq.Chain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Chain
public @interface MultiRequestChain {
    int index();
    String name() default "";
    String description() default "";
    int threadSize() default 1;
    int interval() default 100;
    boolean shareContext() default true;
    boolean moveStopAll() default true;
}

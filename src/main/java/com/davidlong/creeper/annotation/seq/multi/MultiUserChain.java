package com.davidlong.http.annotation.seq.multi;

import com.davidlong.http.annotation.seq.Chain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Chain
public @interface MultiUserChain {
    int index();
    String name() default "";
    String description() default "";
    LoginUser[] users();
    int interval() default 100;
    boolean shareContext() default true;
}

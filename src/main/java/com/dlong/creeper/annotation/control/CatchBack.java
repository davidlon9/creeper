package com.dlong.creeper.annotation.control;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CatchStrategy
public @interface CatchBack {
    int interval() default 0;
    Class[] exceptions() default {};
    String msg() default "";
}

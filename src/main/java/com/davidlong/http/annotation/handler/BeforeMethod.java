package com.davidlong.http.annotation.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeMethod {
    /**
     * SeqRequest的Name,默认是使用注解SeqRequest的方法的name
     * @return
     */
    String value() default "";
}

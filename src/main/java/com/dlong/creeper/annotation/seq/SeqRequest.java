package com.dlong.creeper.annotation.seq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Request
public @interface SeqRequest{
    int index();
    String name() default "";
    String path() default ""; //TODO 添加path到entity
    String description() default "";
}

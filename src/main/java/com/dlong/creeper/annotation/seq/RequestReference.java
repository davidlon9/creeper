package com.dlong.creeper.annotation.seq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestReference {
    int index();
    Class chainClass();
    String requestName() default "";//默认使用被注解的字段名称
    String description() default "";
}

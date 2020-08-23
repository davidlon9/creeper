package com.dlong.creeper.annotation.seq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Chain
public @interface RequestChain {
    int index();
    String name() default "";
    String path() default ""; //TODO 添加chainPath到chainentity
    String description() default "";
}

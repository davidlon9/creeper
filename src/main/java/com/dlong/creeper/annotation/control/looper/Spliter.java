package com.dlong.creeper.annotation.control.looper;

import com.dlong.creeper.execution.spliter.SpliterHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Spliter {
    //集合被分裂的大小
    int splitSize();
    Class<? extends SpliterHandler> spliterHandler();
}

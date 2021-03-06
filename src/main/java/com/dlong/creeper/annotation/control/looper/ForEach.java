package com.dlong.creeper.annotation.control.looper;

import com.dlong.creeper.annotation.control.ExecutionMode;
import com.dlong.creeper.execution.spliter.SpliterHandler;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForEach{
    String itemsContextKey();
    String itemName() default "item";
    Spliter spliter() default @Spliter(splitSize = 1, spliterHandler = SpliterHandler.class);
    ExecutionMode executionMode() default ExecutionMode.SEQUENTIAL;
}

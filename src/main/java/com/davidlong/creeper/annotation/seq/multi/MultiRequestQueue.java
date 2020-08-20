package com.davidlong.creeper.annotation.seq.multi;

import com.davidlong.creeper.annotation.seq.Request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Request
public @interface MultiRequestQueue {
   int index();
   String name() default "";
   String description() default "";
   String queueContextKey();
   String queueElementKey();
   String stopConditionExpr() default "true";

   int threadSize() default 1;
   int delay() default 500;
   boolean shareContext() default true;
   boolean moveStopAll() default true;
}

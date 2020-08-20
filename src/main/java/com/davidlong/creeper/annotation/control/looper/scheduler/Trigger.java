package com.davidlong.creeper.annotation.control.looper.scheduler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Trigger {
    String MILLISECOND="MILLISECOND";
    String SECOND="SECOND";
    String MINUTE="MINUTE";
    String HOUR="HOUR";
    String DAY="DAY";
    String WEEK="WEEK";
    String MONTH="MONTH";
    String YEAR="YEAR";

    String startTimeExpr() default "${time.now()}";
    String endTimeExpr() default "";
    int timeInterval();
    String intervalUnit() default Trigger.SECOND;
    int repeatCount();
    boolean repeatForever() default false;
    int delay() default 0;
}

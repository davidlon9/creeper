package com.davidlong.http.annotation.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonResultParam {
    /**
     * 返回的JsonResponse中,要作为contextparam的key，多级用. 例如：result.token
     * @return
     */
   String value();

    /**
     * Cookie的属性
     */
   String name() default "";
   String defaultValue() default "";
}

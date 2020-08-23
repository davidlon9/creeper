package com.dlong.creeper.annotation.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonResultCookie {
    /**
     * 返回的JsonResponse中,要作为cookie的key，多级用. 例如：result.token
     * @return
     */
   String jsonKey();

    /**
     * Cookie的属性
     */
   String name() default "";
   String defaultValue() default "";
   String domain() default "";
   String path() default "/";
   long expiry() default -1L;

    /**
     * 当请求获得到的cookie可以重复使用时，可以设为cache，只要缓存中有该cookie，就不会再发出请求
     * @return
     */
   boolean cache() default true;
}

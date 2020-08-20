package com.davidlong.http.annotation.seq.multi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
    String usernameKey();
    String passwordKey();
    String usernameVal();
    String passwordVal();
}

package com.davidlong.http.annotation.control;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String driverClass();
    String username();
    String password();
    String url();
}

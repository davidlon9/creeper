package com.davidlong.http.execution.registry;

import com.davidlong.http.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.davidlong.http.execution.handler.CookieResultHandler;
import com.davidlong.http.execution.handler.MultiRequestExecutionResultHandler;
import com.davidlong.http.execution.handler.RequestHandlerMethodExecutionResultHandler;

public class HandlerExecutionResultHandlerRegistry extends RequestExecutionResultHandlerRegistry {
    public HandlerExecutionResultHandlerRegistry() {
        registerExecutionHandler(CookieResultHandler.getInstance());
        registerExecutionHandler(RequestHandlerMethodExecutionResultHandler.getInstance());
        registerExecutionHandler(new MultiRequestExecutionResultHandler());
    }
}

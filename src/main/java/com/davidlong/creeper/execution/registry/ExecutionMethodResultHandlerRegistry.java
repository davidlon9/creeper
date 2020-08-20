package com.davidlong.http.execution.registry;

import com.davidlong.http.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.davidlong.http.execution.handler.CookieResultHandler;
import com.davidlong.http.execution.handler.MultiRequestExecutionResultHandler;

public class ExecutionMethodResultHandlerRegistry extends RequestExecutionResultHandlerRegistry {
    public ExecutionMethodResultHandlerRegistry() {
        registerExecutionHandler(CookieResultHandler.getInstance());
        registerExecutionHandler(new MultiRequestExecutionResultHandler());
    }
}

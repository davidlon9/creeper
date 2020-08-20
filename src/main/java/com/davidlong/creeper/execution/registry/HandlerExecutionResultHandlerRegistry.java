package com.davidlong.creeper.execution.registry;

import com.davidlong.creeper.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.davidlong.creeper.execution.handler.CookieResultHandler;
import com.davidlong.creeper.execution.handler.MultiRequestExecutionResultHandler;
import com.davidlong.creeper.execution.handler.RequestHandlerMethodExecutionResultHandler;

public class HandlerExecutionResultHandlerRegistry extends RequestExecutionResultHandlerRegistry {
    public HandlerExecutionResultHandlerRegistry() {
        registerExecutionHandler(CookieResultHandler.getInstance());
        registerExecutionHandler(RequestHandlerMethodExecutionResultHandler.getInstance());
        registerExecutionHandler(new MultiRequestExecutionResultHandler());
    }
}

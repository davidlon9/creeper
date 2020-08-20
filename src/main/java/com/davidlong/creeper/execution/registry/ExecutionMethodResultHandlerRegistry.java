package com.davidlong.creeper.execution.registry;

import com.davidlong.creeper.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.davidlong.creeper.execution.handler.CookieResultHandler;
import com.davidlong.creeper.execution.handler.MultiRequestExecutionResultHandler;

public class ExecutionMethodResultHandlerRegistry extends RequestExecutionResultHandlerRegistry {
    public ExecutionMethodResultHandlerRegistry() {
        registerExecutionHandler(CookieResultHandler.getInstance());
        registerExecutionHandler(new MultiRequestExecutionResultHandler());
    }
}

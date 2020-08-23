package com.dlong.creeper.execution.registry;

import com.dlong.creeper.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.dlong.creeper.execution.handler.CookieResultHandler;
import com.dlong.creeper.execution.handler.MultiRequestExecutionResultHandler;

public class ExecutionMethodResultHandlerRegistry extends RequestExecutionResultHandlerRegistry {
    public ExecutionMethodResultHandlerRegistry() {
        registerExecutionHandler(CookieResultHandler.getInstance());
        registerExecutionHandler(new MultiRequestExecutionResultHandler());
    }
}

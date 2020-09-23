package com.dlong.creeper.execution.registry;

import com.dlong.creeper.execution.handler.RecorderExecutionResultHandler;
import com.dlong.creeper.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.dlong.creeper.execution.handler.CookieResultHandler;
import com.dlong.creeper.execution.handler.MultiRequestExecutionResultHandler;
import com.dlong.creeper.execution.handler.RequestHandlerMethodExecutionResultHandler;

public class HandlerExecutionResultHandlerRegistry extends RequestExecutionResultHandlerRegistry {
    public HandlerExecutionResultHandlerRegistry() {
        registerExecutionHandler(CookieResultHandler.getInstance());
        registerExecutionHandler(RequestHandlerMethodExecutionResultHandler.getInstance());
        registerExecutionHandler(new MultiRequestExecutionResultHandler());
        registerExecutionHandler(new RecorderExecutionResultHandler());
    }
}

package com.davidlong.http.execution.registry;

import com.davidlong.http.execution.registry.base.ChainExecutionResultHandlerRegistry;
import com.davidlong.http.execution.handler.ChainHandlerMethodExecutionResultHandler;
import com.davidlong.http.execution.handler.MultiChainExecutionResultHandler;

public class DefaultChainResultHandlerRegistry extends ChainExecutionResultHandlerRegistry {
    public DefaultChainResultHandlerRegistry() {
        registerExecutionHandler(ChainHandlerMethodExecutionResultHandler.getInstance());
        registerExecutionHandler(new MultiChainExecutionResultHandler());
    }
}

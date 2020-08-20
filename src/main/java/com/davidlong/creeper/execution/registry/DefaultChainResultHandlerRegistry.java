package com.davidlong.creeper.execution.registry;

import com.davidlong.creeper.execution.registry.base.ChainExecutionResultHandlerRegistry;
import com.davidlong.creeper.execution.handler.ChainHandlerMethodExecutionResultHandler;
import com.davidlong.creeper.execution.handler.MultiChainExecutionResultHandler;

public class DefaultChainResultHandlerRegistry extends ChainExecutionResultHandlerRegistry {
    public DefaultChainResultHandlerRegistry() {
        registerExecutionHandler(ChainHandlerMethodExecutionResultHandler.getInstance());
        registerExecutionHandler(new MultiChainExecutionResultHandler());
    }
}

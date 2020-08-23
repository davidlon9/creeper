package com.dlong.creeper.execution.registry;

import com.dlong.creeper.execution.registry.base.ChainExecutionResultHandlerRegistry;
import com.dlong.creeper.execution.handler.ChainHandlerMethodExecutionResultHandler;
import com.dlong.creeper.execution.handler.MultiChainExecutionResultHandler;

public class DefaultChainResultHandlerRegistry extends ChainExecutionResultHandlerRegistry {
    public DefaultChainResultHandlerRegistry() {
        registerExecutionHandler(ChainHandlerMethodExecutionResultHandler.getInstance());
        registerExecutionHandler(new MultiChainExecutionResultHandler());
    }
}

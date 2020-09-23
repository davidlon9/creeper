package com.dlong.creeper.execution.base;

import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;

public interface ContextExecutor{
    ExecutionResult exeucteRequest(String name);

    ExecutionResult exeucteChain(String name);

    ExecutionResult exeucteRootChain();

    ChainContext getChainContext();
}

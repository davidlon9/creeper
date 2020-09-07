package com.dlong.creeper.execution.base;

import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ChainExecutionResult;
import com.dlong.creeper.model.result.ChainResult;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.HttpResult;

public interface ContextExecutor{
    ExecutionResult exeucteRequest(String name);

    ChainExecutionResult exeucteChain(String name);

    ChainExecutionResult exeucteRootChain();

    ChainContext getChainContext();
}

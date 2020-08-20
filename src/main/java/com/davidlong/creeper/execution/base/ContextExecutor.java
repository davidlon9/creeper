package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.result.ChainResult;
import com.davidlong.creeper.model.result.HttpResult;

public interface ContextExecutor{
    HttpResult exeucteRequest(String name, boolean withHandle);

    HttpResult exeucteRequest(String name);

    ChainResult exeucteChain(String name);

    ChainResult exeucteRootChain();

    ExecutionContext getExecutionContext();
}

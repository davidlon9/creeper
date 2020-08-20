package com.davidlong.http.execution.base;

import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.result.ChainResult;
import com.davidlong.http.model.result.HttpResult;

public interface ContextExecutor{
    HttpResult exeucteRequest(String name, boolean withHandle);

    HttpResult exeucteRequest(String name);

    ChainResult exeucteChain(String name);

    ChainResult exeucteRootChain();

    ExecutionContext getExecutionContext();
}

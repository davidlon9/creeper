package com.dlong.creeper.execution.base;

import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ChainResult;
import com.dlong.creeper.model.result.HttpResult;

public interface ContextExecutor{
    HttpResult exeucteRequest(String name, boolean withHandle);

    HttpResult exeucteRequest(String name);

    ChainResult exeucteChain(String name);

    ChainResult exeucteRootChain();

    ChainContext getChainContext();
}

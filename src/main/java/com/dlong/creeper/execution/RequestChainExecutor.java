package com.dlong.creeper.execution;

import com.dlong.creeper.execution.base.BaseChainExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;

public class RequestChainExecutor extends BaseChainExecutor<RequestChainEntity> {
    public RequestChainExecutor(ChainContext context) {
        super(context);
    }

    @Override
    public ExecutionResult<RequestChainEntity> execute() {
        return super.execute(getRootChain());
    }
}

package com.davidlong.creeper.execution;

import com.davidlong.creeper.execution.base.BaseChainExecutor;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.result.ChainExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;

public class RequestChainExecutor extends BaseChainExecutor<RequestChainEntity> {
    public RequestChainExecutor(ExecutionContext context) {
        super(context);
    }

    @Override
    public ChainExecutionResult<RequestChainEntity> execute() {
        return super.execute(getRootChain());
    }
}

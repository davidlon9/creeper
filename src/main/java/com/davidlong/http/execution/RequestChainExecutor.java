package com.davidlong.http.execution;

import com.davidlong.http.execution.base.BaseChainExecutor;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ChainExecutionResult;
import com.davidlong.http.model.seq.RequestChainEntity;

public class RequestChainExecutor extends BaseChainExecutor<RequestChainEntity> {
    public RequestChainExecutor(ExecutionContext context) {
        super(context);
    }

    @Override
    public ChainExecutionResult<RequestChainEntity> execute() {
        return super.execute(getRootChain());
    }
}

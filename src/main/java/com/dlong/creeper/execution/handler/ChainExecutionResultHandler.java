package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;

/**
 * 执行的前后调用
 */
public interface ChainExecutionResultHandler {
    void beforeExecute(ExecutionResult<? extends RequestChainEntity> executionResult, ChainContext context) throws ExecutionException;

    void afterExecute(ExecutionResult<? extends RequestChainEntity> executionResult,ChainContext context) throws ExecutionException;
}
 
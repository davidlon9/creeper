package com.davidlong.creeper.execution.handler;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;

/**
 * 执行的前后调用
 */
public interface ChainExecutionResultHandler {
    void beforeExecute(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException;

    void afterExecute(ExecutionResult<? extends RequestChainEntity> executionResult,ExecutionContext context) throws ExecutionException;
}
 
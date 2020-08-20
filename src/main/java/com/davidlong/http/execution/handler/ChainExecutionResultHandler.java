package com.davidlong.http.execution.handler;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.RequestChainEntity;

/**
 * 执行的前后调用
 */
public interface ChainExecutionResultHandler {
    void beforeExecute(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException;

    void afterExecute(ExecutionResult<? extends RequestChainEntity> executionResult,ExecutionContext context) throws ExecutionException;
}
 
package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestEntity;

/**
 * 执行的前后调用
 */
public interface RequestExecutionResultHandler {
    void beforeExecute(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException;

    void afterExecute(ExecutionResult<? extends RequestEntity> executionResult,ChainContext context) throws ExecutionException;
}

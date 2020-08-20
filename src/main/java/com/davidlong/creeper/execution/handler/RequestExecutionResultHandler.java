package com.davidlong.creeper.execution.handler;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestEntity;

/**
 * 执行的前后调用
 */
public interface RequestExecutionResultHandler {
    void beforeExecute(ExecutionResult<? extends RequestEntity> executionResult, ExecutionContext context) throws ExecutionException;

    void afterExecute(ExecutionResult<? extends RequestEntity> executionResult,ExecutionContext context) throws ExecutionException;
}

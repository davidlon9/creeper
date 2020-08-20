package com.davidlong.http.execution.handler;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.*;
import com.davidlong.http.model.seq.RequestEntity;

/**
 * 执行的前后调用
 */
public interface RequestExecutionResultHandler {
    void beforeExecute(ExecutionResult<? extends RequestEntity> executionResult,ExecutionContext context) throws ExecutionException;

    void afterExecute(ExecutionResult<? extends RequestEntity> executionResult,ExecutionContext context) throws ExecutionException;
}

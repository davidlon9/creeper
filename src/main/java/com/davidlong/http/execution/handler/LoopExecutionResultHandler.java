package com.davidlong.http.execution.handler;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.LoopExecutionResult;
import com.davidlong.http.model.seq.LoopableEntity;

public interface LoopExecutionResultHandler {
    void  beforeExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ExecutionContext context) throws ExecutionException;

    void afterExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ExecutionContext context) throws ExecutionException;
}

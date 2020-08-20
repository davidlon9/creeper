package com.davidlong.creeper.execution.handler;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.LoopExecutionResult;
import com.davidlong.creeper.model.seq.LoopableEntity;

public interface LoopExecutionResultHandler {
    void  beforeExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ExecutionContext context) throws ExecutionException;

    void afterExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ExecutionContext context) throws Execution
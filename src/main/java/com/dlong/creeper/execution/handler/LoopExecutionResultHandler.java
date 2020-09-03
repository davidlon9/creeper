package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;

public interface LoopExecutionResultHandler {
    void  beforeExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ChainContext context) throws ExecutionException;

    void afterExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ChainContext context) throws ExecutionException;
}

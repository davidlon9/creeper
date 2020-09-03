package com.dlong.creeper.execution.resolver;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;

public interface ExecutionResultResolver {
    ExecutionResult beforeExecuteResolve(ExecutionResult executionResult, ChainContext context) throws ExecutionException;

    ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ChainContext context) throws ExecutionException;
}

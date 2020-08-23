package com.dlong.creeper.execution.resolver;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.result.ExecutionResult;

public interface ExecutionResultResolver {
    ExecutionResult beforeExecuteResolve(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException;

    ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException;
}

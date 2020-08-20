package com.davidlong.creeper.execution.resolver.method;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.davidlong.creeper.model.ExecutionResult;

public class ChainNullAfterResultResolver implements HandlerMethodResultResolver {
    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        return new AutoNextSeqResultResolver().afterExecuteResovle(executionResult,context);
    }
}

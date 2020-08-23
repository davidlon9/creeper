package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;

public class ChainNullAfterResultResolver implements HandlerMethodResultResolver {
    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        return new AutoNextSeqResultResolver().afterExecuteResovle(executionResult,context);
    }
}

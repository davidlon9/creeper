package com.davidlong.http.execution.resolver.method;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.resolver.AutoNextSeqResultResolver;
import com.davidlong.http.model.ExecutionResult;

public class ChainNullAfterResultResolver implements HandlerMethodResultResolver {
    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        return new AutoNextSeqResultResolver().afterExecuteResovle(executionResult,context);
    }
}

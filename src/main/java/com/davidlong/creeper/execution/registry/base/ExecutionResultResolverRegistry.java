package com.davidlong.creeper.execution.registry.base;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.resolver.ExecutionResultResolver;
import com.davidlong.creeper.model.result.ExecutionResult;

import java.util.ArrayList;
import java.util.List;

public class ExecutionResultResolverRegistry{
    private List<ExecutionResultResolver> executionResultResolvers = new ArrayList<>();

    public void registExecutionResultResolver(ExecutionResultResolver resultResolver){
        executionResultResolvers.add(resultResolver);
    }


    public void beforeExecuteResolve(ExecutionResult executionResult,ExecutionContext context) throws ExecutionException {
        for (ExecutionResultResolver handlerMethodResultResolver : executionResultResolvers) {
            handlerMethodResultResolver.beforeExecuteResolve(executionResult,context);
        }
    }

    public void afterExecuteResolve(ExecutionResult executionResult,ExecutionContext context) throws ExecutionException {
        for (ExecutionResultResolver handlerMethodResultResolver : executionResultResolvers) {
            handlerMethodResultResolver.afterExecuteResovle(executionResult,context);
        }
    }

}

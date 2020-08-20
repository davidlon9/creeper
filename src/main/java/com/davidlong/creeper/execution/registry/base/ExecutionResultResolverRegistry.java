package com.davidlong.http.execution.registry.base;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.resolver.ExecutionResultResolver;
import com.davidlong.http.model.ExecutionResult;

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

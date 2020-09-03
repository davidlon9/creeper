package com.dlong.creeper.execution.registry.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.resolver.ExecutionResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;

import java.util.ArrayList;
import java.util.List;

public class ExecutionResultResolverRegistry{
    private List<ExecutionResultResolver> executionResultResolvers = new ArrayList<>();

    public void registExecutionResultResolver(ExecutionResultResolver resultResolver){
        executionResultResolvers.add(resultResolver);
    }


    public void beforeExecuteResolve(ExecutionResult executionResult,ChainContext context) throws ExecutionException {
        for (ExecutionResultResolver handlerMethodResultResolver : executionResultResolvers) {
            handlerMethodResultResolver.beforeExecuteResolve(executionResult,context);
        }
    }

    public void afterExecuteResolve(ExecutionResult executionResult,ChainContext context) throws ExecutionException {
        for (ExecutionResultResolver handlerMethodResultResolver : executionResultResolvers) {
            handlerMethodResultResolver.afterExecuteResovle(executionResult,context);
        }
    }

}

package com.davidlong.http.execution.resolver;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.resolver.method.*;
import com.davidlong.http.model.ExecutionResult;

public class ChainExecutionResultResolver extends SimpleExecutionResultResolver {

    @Override
    public ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        Object afterResult = executionResult.getAfterResult();
        HandlerMethodResultResolver handlerMethodResultResolver;
        if(afterResult==null){
            handlerMethodResultResolver = new ChainNullAfterResultResolver();
        }else{
            Class<?> resolverClass = findResolverClass(afterResultResolvers,afterResult);

            handlerMethodResultResolver = afterResultResolvers.get(resolverClass);
        }

        if(handlerMethodResultResolver != null){
            handlerMethodResultResolver.resolveResult(executionResult,context,afterResult);
        }else{
            logger.warn("after handler entityTarget return a not support type");
        }

        handleNoNext(executionResult,context);
        return executionResult;
    }

}

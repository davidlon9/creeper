package com.davidlong.creeper.execution.resolver;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.resolver.method.*;
import com.davidlong.creeper.model.result.ExecutionResult;

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

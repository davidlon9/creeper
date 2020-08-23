package com.dlong.creeper.execution.resolver;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.resolver.method.*;
import com.dlong.creeper.model.result.ExecutionResult;

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

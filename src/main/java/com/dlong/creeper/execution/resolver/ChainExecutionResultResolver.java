package com.dlong.creeper.execution.resolver;

import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.resolver.method.*;
import com.dlong.creeper.model.result.ExecutionResult;

public class ChainExecutionResultResolver extends SimpleExecutionResultResolver {
    static{
        afterResultResolvers.put(MoveAction.class,new ChainMoveStrategyAfterResultResolver());
    }

    @Override
    public ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ChainContext context) throws ExecutionException {
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

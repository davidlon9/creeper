package com.davidlong.creeper.execution.resolver;

import com.davidlong.creeper.annotation.control.FailedStrategy;
import com.davidlong.creeper.control.MoveAction;
import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.resolver.method.*;
import com.davidlong.creeper.model.ExecutionResult;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SimpleExecutionResultResolver extends BaseExecutionResultResolver{
    protected static Map<Class<?>,HandlerMethodResultResolver> beforeResultResolvers = new HashMap<>();

    protected static Map<Class<?>,HandlerMethodResultResolver> afterResultResolvers = new HashMap<>();

    protected Logger logger=Logger.getLogger(SimpleExecutionResultResolver.class);

    static{
        beforeResultResolvers.put(Boolean.class,new SimpleBeforeResultResolver());
        beforeResultResolvers.put(MoveAction.class,new MoveStrategyBeforeResultResolver());

        afterResultResolvers.put(Boolean.class,new DefaultAfterResultResolver());
        afterResultResolvers.put(FailedStrategy.class,new FailedStrategyAfterResultResolver());
        afterResultResolvers.put(MoveAction.class,new MoveStrategyAfterResultResolver());
        afterResultResolvers.put(String.class,new StringAfterResultResolver());
    }

    @Override
    public ExecutionResult beforeExecuteResolve(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        Object beforeResult = executionResult.getBeforeResult();
        if(beforeResult==null){
            return executionResult;
        }

        Class<?> resolverClass = findResolverClass(beforeResultResolvers,beforeResult);
        //TODO 限制返回类型
        HandlerMethodResultResolver handlerMethodResultResolver = beforeResultResolvers.get(resolverClass);

        if(handlerMethodResultResolver != null){
            handlerMethodResultResolver.resolveResult(executionResult,context,beforeResult);
        }else{
            logger.warn("before handler entityTarget return a not support type");
        }
        return executionResult;
    }

    @Override
    public ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        Object afterResult = executionResult.getAfterResult();
        HandlerMethodResultResolver handlerMethodResultResolver;
        if(afterResult==null){
            handlerMethodResultResolver = new DefaultAfterResultResolver();
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

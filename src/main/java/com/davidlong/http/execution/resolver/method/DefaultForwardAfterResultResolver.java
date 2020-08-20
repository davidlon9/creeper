package com.davidlong.http.execution.resolver.method;

import com.davidlong.http.control.ForwardAction;
import com.davidlong.http.control.TerminateAction;
import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ExecutionResult;
import org.apache.log4j.Logger;

public class DefaultForwardAfterResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(DefaultForwardAfterResultResolver.class);

    //默认终结执行
    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        if(methodResult == null){
            logger.warn("DefaultForwardAfterResultResolver resolve null as Forward");
            new MoveStrategyAfterResultResolver().resolveResult(executionResult,context, new ForwardAction());
            return executionResult;
        }
        if(methodResult instanceof Boolean){
            if(!(Boolean)methodResult){
                new MoveStrategyAfterResultResolver().resolveResult(executionResult,context, new TerminateAction(true));
            }else{
                new MoveStrategyAfterResultResolver().resolveResult(executionResult,context, new ForwardAction());
            }
        }
        return executionResult;
    }
}

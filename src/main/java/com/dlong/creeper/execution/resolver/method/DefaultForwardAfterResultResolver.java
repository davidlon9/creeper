package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.control.ForwardAction;
import com.dlong.creeper.control.TerminateAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import org.apache.log4j.Logger;

public class DefaultForwardAfterResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(DefaultForwardAfterResultResolver.class);

    //默认终结执行
    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
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

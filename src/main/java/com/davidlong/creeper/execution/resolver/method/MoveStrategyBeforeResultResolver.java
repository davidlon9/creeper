package com.davidlong.creeper.execution.resolver.method;

import com.davidlong.creeper.control.ContinueAction;
import com.davidlong.creeper.control.MoveAction;
import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

public class MoveStrategyBeforeResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(MoveStrategyBeforeResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        SequentialEntity next=null;
        if(methodResult instanceof MoveAction){
            MoveAction moveAction = (MoveAction) methodResult;
            if (moveAction instanceof ContinueAction) {
                next = executionResult.getOrginalSeq();
                executionResult.setSkipExecute(true);
            }
        }
        executionResult.setNextSeq(next);
        return executionResult;
    }
}

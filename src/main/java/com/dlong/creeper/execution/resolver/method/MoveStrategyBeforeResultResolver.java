package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.control.ContinueAction;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.SequentialEntity;
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

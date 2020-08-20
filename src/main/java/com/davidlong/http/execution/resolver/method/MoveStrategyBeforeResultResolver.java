package com.davidlong.http.execution.resolver.method;

import com.davidlong.http.control.ContinueAction;
import com.davidlong.http.control.MoveAction;
import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.SequentialEntity;
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

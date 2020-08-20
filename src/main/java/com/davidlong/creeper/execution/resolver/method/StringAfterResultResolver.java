package com.davidlong.creeper.execution.resolver.method;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.seq.SequentialEntity;

public class StringAfterResultResolver implements HandlerMethodResultResolver {

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        SequentialEntity next=null;
        if(methodResult instanceof String){
            next = context.getSequntialFinder().findSeqByName((String) methodResult,executionResult.getOrginalParent());
        }
        executionResult.setNextSeq(next);
        return executionResult;
    }
}

package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.SequentialEntity;

public class StringAfterResultResolver implements HandlerMethodResultResolver {

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        SequentialEntity next=null;
        if(methodResult instanceof String){
            next = context.getSequntialFinder().findSeqByName((String) methodResult,executionResult.getOrginalParent());
        }
        executionResult.setNextSeq(next);
        return executionResult;
    }
}

package com.davidlong.http.execution.resolver.method;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.SequentialEntity;

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

package com.davidlong.http.execution.resolver;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.SequentialEntity;

public class AutoNextSeqResultResolver implements ExecutionResultResolver {
//    private static Logger logger=Logger.getLogger(AutoNextSeqResultResolver.class);

    @Override
    public ExecutionResult beforeExecuteResolve(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        return executionResult;
    }

    @Override
    public ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        if (executionResult.isFailed()) {
            return executionResult;
        }
        SequentialEntity next = context.getSequntialFinder().findNextSeq(executionResult.getOrginalSeq());
        executionResult.setNextSeq(next);
        return executionResult;
    }
}

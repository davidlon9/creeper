package com.dlong.creeper.execution.resolver;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.SequentialEntity;

public class AutoNextSeqResultResolver implements ExecutionResultResolver {
//    private static Logger logger=Logger.getLogger(AutoNextSeqResultResolver.class);

    @Override
    public ExecutionResult beforeExecuteResolve(ExecutionResult executionResult, ChainContext context) throws ExecutionException {
        return executionResult;
    }

    @Override
    public ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ChainContext context) throws ExecutionException {
        if (executionResult.isFailed()) {
            return executionResult;
        }
        SequentialEntity next = context.getSequntialFinder().findNextSeq(executionResult.getOrginalSeq());
        executionResult.setNextSeq(next);
        return executionResult;
    }
}

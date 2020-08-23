package com.dlong.creeper.execution.resolver;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.MultiExecutionResult;
import com.dlong.creeper.model.seq.SequentialEntity;
import com.dlong.creeper.model.seq.multi.MultiRequestEntity;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.List;

//TODO 未完善
public class MultiExecutionResultResolver implements ExecutionResultResolver {
    private static Logger logger=Logger.getLogger(MultiExecutionResultResolver.class);

    @Override
    public ExecutionResult beforeExecuteResolve(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        return null;
    }

    @Override
    public ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        Assert.isInstanceOf(MultiExecutionResult.class,executionResult,"ExecutionResult is not a instance of MultiExecutionResult");

        MultiExecutionResult<MultiRequestEntity> multiExecutionResult = (MultiExecutionResult<MultiRequestEntity>) executionResult;
        List<ExecutionResult> threadResults = multiExecutionResult.getThreadResults();
        for (ExecutionResult innerResult : threadResults) {
            if (innerResult.isFailed()) {
                continue;
            }
            SequentialEntity nextSeq = innerResult.getNextSeq();
            if(nextSeq!=null){
                multiExecutionResult.setNextSeq(nextSeq);
            }
        }
        return multiExecutionResult;
    }
}

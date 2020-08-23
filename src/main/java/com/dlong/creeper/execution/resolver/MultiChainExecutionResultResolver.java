package com.dlong.creeper.execution.resolver;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.MultiChainExecutionResult;
import com.dlong.creeper.model.seq.SequentialEntity;
import com.dlong.creeper.model.seq.multi.MultiRequestChainEntity;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.List;

//TODO 未完善
public class MultiChainExecutionResultResolver implements ExecutionResultResolver {
    private static Logger logger=Logger.getLogger(MultiChainExecutionResultResolver.class);

    @Override
    public ExecutionResult beforeExecuteResolve(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        return null;
    }

    @Override
    public ExecutionResult afterExecuteResovle(ExecutionResult executionResult, ExecutionContext context) throws ExecutionException {
        Assert.isInstanceOf(MultiChainExecutionResult.class,executionResult,"ExecutionResult is not a instance of MultiExecutionResult");

        MultiChainExecutionResult<MultiRequestChainEntity> multiExecutionResult = (MultiChainExecutionResult<MultiRequestChainEntity>) executionResult;
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

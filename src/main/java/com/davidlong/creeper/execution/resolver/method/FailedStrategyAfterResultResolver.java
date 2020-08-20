package com.davidlong.creeper.execution.resolver.method;

import com.davidlong.creeper.annotation.control.FailedStrategy;
import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestEntity;
import com.davidlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

public class FailedStrategyAfterResultResolver implements HandlerMethodResultResolver {
    private static Logger logger = Logger.getLogger(FailedStrategyAfterResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        SequentialEntity seq = executionResult.getOrginalSeq();
        SequentialEntity next=null;
        if(seq instanceof RequestEntity){
            if(methodResult instanceof FailedStrategy){
                FailedStrategy failedStrategy = (FailedStrategy) methodResult;
                int failedNextIndex = getFailedNextIndex(executionResult.getOrginalIndex(), failedStrategy);
                next = context.getSequntialFinder().findSeqByFixedIndex(failedNextIndex,seq.getParent());
            }
        }
        executionResult.setNextSeq(next);
        return executionResult;
    }

    public int getFailedNextIndex(int currentIdx,FailedStrategy failedStrategy) throws ExecutionException {
        if(failedStrategy ==null || FailedStrategy.FORWARD.equals(failedStrategy) || FailedStrategy.SUCCESS.equals(failedStrategy)){
            return ++currentIdx;
        }else if(FailedStrategy.BACK.equals(failedStrategy)){
            return --currentIdx;
        }else if(FailedStrategy.RESTART.equals(failedStrategy)){
            return 1;
        }else if(FailedStrategy.TERMINATE.equals(failedStrategy)){
            return -1;
        }else if(FailedStrategy.RETRY.equals(failedStrategy)){
            Integer wait = failedStrategy.getWait();
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return currentIdx;
        }else if("JUMP".equals(failedStrategy.getName())){
            return failedStrategy.getTo();
        }else if(FailedStrategy.MUTABLE.equals(failedStrategy)){
            throw new ExecutionException("MUTABLE failed strategy can't as a return value please try others");
        }else{
            throw new ExecutionException("unkown failed strategy "+ failedStrategy +" please try others");
        }
    }
}

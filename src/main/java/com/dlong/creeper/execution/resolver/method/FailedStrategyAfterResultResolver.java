package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.annotation.control.ExceptionStrategy;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

public class FailedStrategyAfterResultResolver implements HandlerMethodResultResolver {
    private static Logger logger = Logger.getLogger(FailedStrategyAfterResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        SequentialEntity seq = executionResult.getOrginalSeq();
        SequentialEntity next=null;
        if(seq instanceof RequestEntity){
            if(methodResult instanceof ExceptionStrategy){
                ExceptionStrategy exceptionStrategy = (ExceptionStrategy) methodResult;
                int failedNextIndex = getFailedNextIndex(executionResult.getOrginalIndex(), exceptionStrategy);
                next = context.getSequntialFinder().findSeqByFixedIndex(failedNextIndex,seq.getParent());
            }
        }
        executionResult.setNextSeq(next);
        return executionResult;
    }

    public int getFailedNextIndex(int currentIdx,ExceptionStrategy exceptionStrategy) throws ExecutionException {
        if(exceptionStrategy ==null || ExceptionStrategy.FORWARD.equals(exceptionStrategy) || ExceptionStrategy.SUCCESS.equals(exceptionStrategy)){
            return ++currentIdx;
        }else if(ExceptionStrategy.BACK.equals(exceptionStrategy)){
            return --currentIdx;
        }else if(ExceptionStrategy.RESTART.equals(exceptionStrategy)){
            return 1;
        }else if(ExceptionStrategy.TERMINATE.equals(exceptionStrategy)){
            return -1;
        }else if(ExceptionStrategy.RETRY.equals(exceptionStrategy)){
            Integer wait = exceptionStrategy.getWait();
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return currentIdx;
        }else if("JUMP".equals(exceptionStrategy.getName())){
            return exceptionStrategy.getTo();
        }else if(ExceptionStrategy.MUTABLE.equals(exceptionStrategy)){
            throw new ExecutionException("MUTABLE failed strategy can't as a return value please try others");
        }else{
            throw new ExecutionException("unkown failed strategy "+ exceptionStrategy +" please try others");
        }
    }
}

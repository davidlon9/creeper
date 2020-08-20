package com.davidlong.creeper.execution.resolver.method;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

public class SimpleBeforeResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(SimpleBeforeResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        SequentialEntity seq = executionResult.getOrginalSeq();
        SequentialEntity next=null;
        if(methodResult instanceof Boolean && !(Boolean) methodResult){
            RequestChainEntity parent = seq.getParent();
            if(parent!=null){
                int index = seq.getIndex();
                next = context.getSequntialFinder().findSeqByFixedIndex(++index, parent);
                logger.info("before handler return a false result, handler will execute next sequential "+next);
            }else{
                logger.warn("execution still skiped but can't resolve next for root entity "+seq);
            }
            executionResult.setSkipExecute(true);
        }
        executionResult.setNextSeq(next);
        return executionResult;
    }
}

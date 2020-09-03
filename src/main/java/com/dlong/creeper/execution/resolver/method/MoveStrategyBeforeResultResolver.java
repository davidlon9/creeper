package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.control.ContinueAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.exception.UnsupportedReturnTypeException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

public class MoveStrategyBeforeResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(MoveStrategyBeforeResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        SequentialEntity next;
        if(methodResult instanceof ContinueAction){
            next = executionResult.getOrginalSeq();
            executionResult.setSkipExecute(true);
            executionResult.setNextSeq(next);
            logger.warn("before handler return ContinueAction to skip current loop in loop execution");
            return executionResult;
        }else{
            throw new UnsupportedReturnTypeException("BeforeHandler only support ContinueAction to skip current loop in loop execution, to skip normal execution please return Boolean value");
        }
    }
}

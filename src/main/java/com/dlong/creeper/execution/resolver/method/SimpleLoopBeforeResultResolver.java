package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import org.apache.log4j.Logger;

public class SimpleLoopBeforeResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(SimpleLoopBeforeResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        if(methodResult instanceof Boolean && !(Boolean) methodResult){
            logger.info("before handler return a false,looper will continue next loop");
            executionResult.setSkipExecute(true);
        }
        return executionResult;
    }
}

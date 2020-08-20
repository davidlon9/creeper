package com.davidlong.http.execution.resolver.method;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ExecutionResult;
import org.apache.log4j.Logger;

public class SimpleLoopBeforeResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(SimpleLoopBeforeResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        if(methodResult instanceof Boolean && !(Boolean) methodResult){
            logger.info("before handler return a false,looper will continue next loop");
            executionResult.setSkipExecute(true);
        }
        return executionResult;
    }
}

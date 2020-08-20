package com.davidlong.http.execution.registry.base;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.handler.LoopExecutionResultHandler;
import com.davidlong.http.model.LoopExecutionResult;
import com.davidlong.http.model.seq.LoopableEntity;

import java.util.ArrayList;
import java.util.List;

public class LoopExecutionResultHandlerRegistry {
    private List<LoopExecutionResultHandler> loopExecutionResultHandlers = new ArrayList<>();

    /**
     * 调用executionHandler的beforeExecute方法
     *
     * 默认调用以下handler
     * 1.CookieResultHandler
     * 2.RequestHandlerMethodExecutionResultHandler
     */
    public void invokeBeforeExecutionHandler(LoopExecutionResult<? extends LoopableEntity> executionResult, ExecutionContext context) throws ExecutionException {
        for (LoopExecutionResultHandler loopExecutionResultHandler : loopExecutionResultHandlers) {
            loopExecutionResultHandler.beforeExecute(executionResult,context);
        }
    }

    /**
     * 调用executionHandler的afterExecute方法
     *
     * 默认调用以下handler
     * 1.RequestHandlerMethodExecutionResultHandler
     */
    public void invokeAfterExecutionHandler(LoopExecutionResult<? extends LoopableEntity> executionResult,ExecutionContext context) throws ExecutionException {
        for (LoopExecutionResultHandler loopExecutionResultHandler : loopExecutionResultHandlers) {
            loopExecutionResultHandler.afterExecute(executionResult,context);
        }
    }

    public void registerExecutionHandler(LoopExecutionResultHandler loopExecutionResultHandler){
        this.loopExecutionResultHandlers.add(loopExecutionResultHandler);
    }
}

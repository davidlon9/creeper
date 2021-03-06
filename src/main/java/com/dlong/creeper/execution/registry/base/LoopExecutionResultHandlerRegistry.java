package com.dlong.creeper.execution.registry.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.handler.LoopExecutionResultHandler;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;

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
    public void invokeBeforeExecutionHandler(LoopExecutionResult<? extends LoopableEntity> executionResult, ChainContext context) throws ExecutionException {
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
    public void invokeAfterExecutionHandler(LoopExecutionResult<? extends LoopableEntity> executionResult,ChainContext context) throws ExecutionException {
        for (LoopExecutionResultHandler loopExecutionResultHandler : loopExecutionResultHandlers) {
            loopExecutionResultHandler.afterExecute(executionResult,context);
        }
    }

    public void registerExecutionHandler(LoopExecutionResultHandler loopExecutionResultHandler){
        this.loopExecutionResultHandlers.add(loopExecutionResultHandler);
    }
}

package com.davidlong.http.execution.registry.base;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.handler.ChainExecutionResultHandler;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.RequestChainEntity;

import java.util.ArrayList;
import java.util.List;

public class ChainExecutionResultHandlerRegistry {
    private List<ChainExecutionResultHandler> chainExecutionResultHandlers = new ArrayList<>();

    /**
     * 调用executionHandler的beforeExecute方法
     *
     * 默认调用以下handler
     * 1.CookieResultHandler
     * 2.RequestHandlerMethodExecutionResultHandler
     */
    public void invokeBeforeExecutionHandler(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException {
        for (ChainExecutionResultHandler chainExecutionResultHandler : chainExecutionResultHandlers) {
            chainExecutionResultHandler.beforeExecute(executionResult,context);
        }
    }

    /**
     * 调用executionHandler的afterExecute方法
     *
     * 默认调用以下handler
     * 1.RequestHandlerMethodExecutionResultHandler
     */
    public void invokeAfterExecutionHandler(ExecutionResult<? extends RequestChainEntity> executionResult,ExecutionContext context) throws ExecutionException {
        for (ChainExecutionResultHandler chainExecutionResultHandler : chainExecutionResultHandlers) {
            chainExecutionResultHandler.afterExecute(executionResult,context);
        }
    }

    public void registerExecutionHandler(ChainExecutionResultHandler chainExecutionResultHandler){
        this.chainExecutionResultHandlers.add(chainExecutionResultHandler);
    }
}

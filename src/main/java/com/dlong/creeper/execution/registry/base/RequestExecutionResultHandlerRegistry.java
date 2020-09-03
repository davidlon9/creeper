package com.dlong.creeper.execution.registry.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.handler.RequestExecutionResultHandler;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestEntity;

import java.util.ArrayList;
import java.util.List;

public class RequestExecutionResultHandlerRegistry {
    private List<RequestExecutionResultHandler> requestExecutionResultHandlers = new ArrayList<>();

    /**
     * 调用executionHandler的beforeExecute方法
     *
     * 默认调用以下handler
     * 1.CookieResultHandler
     * 2.RequestHandlerMethodExecutionResultHandler
     */
    public void invokeBeforeExecutionHandler(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException {
        for (RequestExecutionResultHandler requestExecutionResultHandler : requestExecutionResultHandlers) {
            requestExecutionResultHandler.beforeExecute(executionResult,context);
        }
    }

    public void invokeBeforeExecutionHandler(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context , List<Class<? extends RequestExecutionResultHandler>> excludes) throws ExecutionException {
        for (RequestExecutionResultHandler requestExecutionResultHandler : requestExecutionResultHandlers) {
            if(!excludes.contains(requestExecutionResultHandler.getClass())){
                requestExecutionResultHandler.beforeExecute(executionResult,context);
            }
        }
    }


    /**
     * 调用executionHandler的afterExecute方法
     *
     * 默认调用以下handler
     * 1.RequestHandlerMethodExecutionResultHandler
     */
    public void invokeAfterExecutionHandler(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException {
        for (RequestExecutionResultHandler requestExecutionResultHandler : requestExecutionResultHandlers) {
            requestExecutionResultHandler.afterExecute(executionResult,context);
        }
    }

    public void invokeAfterExecutionHandler(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context , List<Class<? extends RequestExecutionResultHandler>> excludes) throws ExecutionException {
        for (RequestExecutionResultHandler requestExecutionResultHandler : requestExecutionResultHandlers) {
            if(!excludes.contains(requestExecutionResultHandler.getClass())){
                requestExecutionResultHandler.afterExecute(executionResult,context);
            }
        }
    }

    public void registerExecutionHandler(RequestExecutionResultHandler requestExecutionResultHandler){
        this.requestExecutionResultHandlers.add(requestExecutionResultHandler);
    }
}

package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.handler.entity.AfterHandler;
import com.dlong.creeper.execution.handler.entity.BeforeHandler;
import com.dlong.creeper.execution.handler.entity.ExecutionHandler;
import com.dlong.creeper.execution.handler.info.RequestAfterMethodArgumentInfo;
import com.dlong.creeper.execution.handler.info.RequestBeforeMethodArgumentInfo;
import com.dlong.creeper.execution.resolver.ExecutionResultResolver;
import com.dlong.creeper.execution.resolver.LoopExecutionResultResolver;
import com.dlong.creeper.execution.resolver.SimpleExecutionResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.HandlerMethodWrapper;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.control.Looper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;

public class RequestHandlerMethodExecutionResultHandler implements RequestExecutionResultHandler {
    private static Logger logger=Logger.getLogger(RequestHandlerMethodExecutionResultHandler.class);

    //默认执行的ResultResolver,保证后续执行的handler可以获取到默认解析出的nextSeq
    private ExecutionResultResolver handlerResultResolver = new SimpleExecutionResultResolver();

    private ExecutionResultResolver loopResultResolver = new LoopExecutionResultResolver();

    private volatile static RequestHandlerMethodExecutionResultHandler singleton;

    private RequestHandlerMethodExecutionResultHandler() {
    }

    public static RequestHandlerMethodExecutionResultHandler getInstance() {
        if (singleton == null) {
            synchronized (RequestHandlerMethodExecutionResultHandler.class) {
                if (singleton == null) {
                    singleton = new RequestHandlerMethodExecutionResultHandler();
                }
            }
        }
        return singleton;
    }

    @Override
    public void beforeExecute(ExecutionResult<? extends RequestEntity> executionResult,ChainContext context) throws ExecutionException {
        //调用BeforeHandler
        invokeBeforeHandler(executionResult,context);
        beforeResolve(executionResult, context);
    }

    @Override
    public void afterExecute(ExecutionResult<? extends RequestEntity> executionResult,ChainContext context) throws ExecutionException {
        //调用AfterHandler
        invokeAfterHandler(executionResult,context);
        afterResolve(executionResult, context);
    }

    private void beforeResolve(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException {
        Looper looper = executionResult.getOrginalSeq().getLooper();
        if(looper != null){
            loopResultResolver.beforeExecuteResolve(executionResult,context);
        }else{
            handlerResultResolver.beforeExecuteResolve(executionResult,context);
        }
    }

    private void afterResolve(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException {
        Looper looper = executionResult.getOrginalSeq().getLooper();
        if(looper != null){
            loopResultResolver.afterExecuteResovle(executionResult,context);
        }else{
            handlerResultResolver.afterExecuteResovle(executionResult,context);
        }
    }

    private void invokeBeforeHandler(ExecutionResult<? extends RequestEntity> executionResult,ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = executionResult.getOrginalSeq();
        Object handlerResult = null;
        try {
            Object handler = requestEntity.getExecutionHandler();
            if(handler != null && handler instanceof ExecutionHandler){
                handlerResult = ((ExecutionHandler) handler).beforeHandle(requestEntity.getRequest(),context);
                executionResult.setBeforeResult(handlerResult);
                return;
            }

            Object beforeHandler = requestEntity.getBeforeHandler();
            if(beforeHandler == null){
                return;
            }

            if(beforeHandler instanceof Method){
                handlerResult = buildBeforeHandlerMethodWrapper(requestEntity,context).invokeHandlerMethod();
            }else if(beforeHandler instanceof BeforeHandler){
                handlerResult = ((BeforeHandler) beforeHandler).beforeHandle(requestEntity.getRequest(), context);
            }
            executionResult.setBeforeResult(handlerResult);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private void invokeAfterHandler(ExecutionResult<? extends RequestEntity> executionResult, ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = executionResult.getOrginalSeq();
        try {
            Object handlerResult = null;
            Object handler = requestEntity.getExecutionHandler();
            if(handler != null && handler instanceof ExecutionHandler){
                handlerResult = ((ExecutionHandler) handler).afterHandle(executionResult.getHttpResponse(),context);
                executionResult.setAfterResult(handlerResult);
                return;
            }

            Object afterHandler = requestEntity.getAfterHandler();
            if(afterHandler == null){
                logger.warn(executionResult.getOrginalSeq()+" does not have a after handler, after result is null");
                return;
            }

            if(afterHandler instanceof Method){
                handlerResult = buildAfterHandlerMethodWrapper(executionResult,context).invokeHandlerMethod();
            }else if(afterHandler instanceof AfterHandler){
                handlerResult = ((AfterHandler) afterHandler).afterHandle(executionResult.getHttpResponse(), context);
            }
            executionResult.setAfterResult(handlerResult);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private HandlerMethodWrapper buildBeforeHandlerMethodWrapper(RequestEntity requestEntity,ChainContext context){
        HandlerMethodWrapper handlerMethodWrapper = new HandlerMethodWrapper();
        Method method = (Method) requestEntity.getBeforeHandler();
        handlerMethodWrapper.setMethod(method);
        Object[] args = new RequestBeforeMethodArgumentInfo(context).fetchHandlerMethodArgs(method,requestEntity.getRequest());
        //TODO RequestEntityODO 需要缓存
        handlerMethodWrapper.setArgs(args);
        Object currentChainInstance = requestEntity.getParent().getChainInstance();
        handlerMethodWrapper.setInvokeInstance(currentChainInstance);
        return handlerMethodWrapper;
    }

    private HandlerMethodWrapper buildAfterHandlerMethodWrapper(ExecutionResult<? extends RequestEntity> executionResult,ChainContext context){
        HandlerMethodWrapper handlerMethodWrapper = new HandlerMethodWrapper();
        RequestEntity requestEntity = executionResult.getOrginalSeq();
        Method method = (Method) requestEntity.getAfterHandler();
        handlerMethodWrapper.setMethod(method);
        //TODO RequestEntityODO 需要缓存
        Object[] args = new RequestAfterMethodArgumentInfo(context).fetchHandlerMethodArgs(method, executionResult.getHttpResponse());
        handlerMethodWrapper.setArgs(args);
        Object currentChainInstance = requestEntity.getParent().getChainInstance();
        handlerMethodWrapper.setInvokeInstance(currentChainInstance);
        return handlerMethodWrapper;
    }
}

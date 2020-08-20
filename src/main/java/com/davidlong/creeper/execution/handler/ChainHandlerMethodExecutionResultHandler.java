package com.davidlong.creeper.execution.handler;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.handler.entity.*;
import com.davidlong.creeper.execution.handler.info.HandlerMethodArgumentInfo;
import com.davidlong.creeper.execution.resolver.ChainExecutionResultResolver;
import com.davidlong.creeper.execution.resolver.ExecutionResultResolver;
import com.davidlong.creeper.execution.resolver.LoopExecutionResultResolver;
import com.davidlong.creeper.model.*;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.control.Looper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;

public class ChainHandlerMethodExecutionResultHandler implements ChainExecutionResultHandler {
    private static Logger logger=Logger.getLogger(ChainHandlerMethodExecutionResultHandler.class);

    //默认执行的ResultResolver,保证后续执行的handler可以获取到默认解析出的nextSeq
    private ExecutionResultResolver handlerResultResolver=new ChainExecutionResultResolver();

    private ExecutionResultResolver loopResultResolver=new LoopExecutionResultResolver();

    private volatile static ChainHandlerMethodExecutionResultHandler singleton;

    private ChainHandlerMethodExecutionResultHandler() {
    }

    public static ChainHandlerMethodExecutionResultHandler getInstance() {
        if (singleton == null) {
            synchronized (ChainHandlerMethodExecutionResultHandler.class) {
                if (singleton == null) {
                    singleton = new ChainHandlerMethodExecutionResultHandler();
                }
            }
        }
        return singleton;
    }

    @Override
    public void beforeExecute(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException {
        //调用BeforeHandler
        invokeBeforeHandler(executionResult,context);
        beforeResolve(executionResult, context);
    }

    @Override
    public void afterExecute(ExecutionResult<? extends RequestChainEntity> executionResult,ExecutionContext context) throws ExecutionException {
        //调用AfterHandler
        invokeAfterHandler(executionResult,context);
        afterResolve(executionResult, context);
    }

    private void beforeResolve(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException {
        Looper looper = executionResult.getOrginalSeq().getLooper();
        if(looper==null){
            handlerResultResolver.beforeExecuteResolve(executionResult,context);
        }else{
            loopResultResolver.beforeExecuteResolve(executionResult,context);
        }
    }

    private void afterResolve(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException {
        Looper looper = executionResult.getOrginalSeq().getLooper();
        if(looper==null){
            handlerResultResolver.afterExecuteResovle(executionResult,context);
        }else{
            loopResultResolver.afterExecuteResovle(executionResult,context);
        }
    }

    private void invokeBeforeHandler(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException {
        RequestChainEntity chainEntity = executionResult.getOrginalSeq();
        Object handlerResult = null;
        try {
            Object handler = chainEntity.getExecutionHandler();
            if(handler != null && handler instanceof ChainExecutionHandler){
                handlerResult = ((ChainExecutionHandler) handler).beforeHandle(context);
                executionResult.setBeforeResult(handlerResult);
                return;
            }

            Object beforeHandler = chainEntity.getBeforeHandler();
            if(beforeHandler == null){
                return;
            }

            if(beforeHandler instanceof Method){
                handlerResult = buildHandlerMethodWrapper(chainEntity, (Method) beforeHandler,context).invokeHandlerMethod();
            }else if(beforeHandler instanceof ChainBeforeHandler){
                    handlerResult = ((ChainBeforeHandler) beforeHandler).beforeHandle(context);
            }
            executionResult.setBeforeResult(handlerResult);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private void invokeAfterHandler(ExecutionResult<? extends RequestChainEntity> executionResult, ExecutionContext context) throws ExecutionException {
        RequestChainEntity chainEntity = executionResult.getOrginalSeq();
        Object handlerResult = null;
        try {
            Object handler = chainEntity.getExecutionHandler();
            if(handler != null && handler instanceof ChainExecutionHandler){
                handlerResult = ((ChainExecutionHandler) handler).afterHandle(context);
                executionResult.setAfterResult(handlerResult);
                return;
            }

            Object afterHandler = chainEntity.getAfterHandler();
            if(afterHandler == null){
                if(chainEntity.getLooper() != null) logger.warn("Loop chain dose't have a after handler, looper will proceeding loop");
                return;
            }

            if(afterHandler instanceof Method){
                HandlerMethodWrapper methodWrapper = buildHandlerMethodWrapper(chainEntity, (Method) afterHandler,context);
                handlerResult = methodWrapper.invokeHandlerMethod();
            }else if(afterHandler instanceof ChainAfterHandler){
                handlerResult = ((ChainAfterHandler) afterHandler).afterHandle(context);
            }
            executionResult.setAfterResult(handlerResult);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private HandlerMethodWrapper buildHandlerMethodWrapper(RequestChainEntity chainEntity, Method handlerMethod,ExecutionContext context) {
        HandlerMethodWrapper handlerMethodWrapper = new HandlerMethodWrapper();
        handlerMethodWrapper.setMethod(handlerMethod);
        Object[] args = new HandlerMethodArgumentInfo(context).fetchHandlerMethodArgs(handlerMethod);
        //TODO 需要缓存
        handlerMethodWrapper.setArgs(args);
        Object currentChainInstance = chainEntity.getChainInstance();
        handlerMethodWrapper.setInvokeInstance(currentChainInstance);
        return handlerMethodWrapper;
    }

}

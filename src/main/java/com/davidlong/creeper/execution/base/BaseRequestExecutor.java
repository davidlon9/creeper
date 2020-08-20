package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.control.IntervalAction;
import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.handler.RequestHandlerMethodExecutionResultHandler;
import com.davidlong.creeper.execution.handler.info.ExecutionMethodArgumentInfo;
import com.davidlong.creeper.execution.registry.ExecutionMethodResultHandlerRegistry;
import com.davidlong.creeper.execution.registry.HandlerExecutionResultHandlerRegistry;
import com.davidlong.creeper.execution.registry.base.ExecutionResultResolverRegistry;
import com.davidlong.creeper.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.davidlong.creeper.execution.request.DefaultRequestBuilder;
import com.davidlong.creeper.execution.request.HttpRequestBuilder;
import com.davidlong.creeper.execution.resolver.SimpleExecutionResultResolver;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.HandlerMethodWrapper;
import com.davidlong.creeper.model.log.ResponseLogInfo;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.RequestEntity;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;


public class BaseRequestExecutor<T extends RequestEntity> extends AbstractLoopableExecutor<T> implements RequestExecutor<T>{
    private HttpRequestBuilder requestBuilder;

    private RequestExecutionResultHandlerRegistry handlerExecuteHandlerRegistry;
    private ExecutionResultResolverRegistry handlerExecuteResultResolverRegistry;

    private RequestExecutionResultHandlerRegistry executionMethodHandlerRegistry;
    private ExecutionResultResolverRegistry executionMethodResultResolverRegistry;

    private static Logger logger=Logger.getLogger(BaseRequestExecutor.class);

    public BaseRequestExecutor(ExecutionContext context) {
        this(context,false);
    }

    public BaseRequestExecutor(ExecutionContext context, boolean isMultiThread) {
        super(context, isMultiThread);
        init();
    }

    public BaseRequestExecutor(ExecutionContext context, HttpRequestBuilder requestBuilder) {
        this(context);
        this.requestBuilder = requestBuilder;
    }

    private void init(){
        this.requestBuilder = new DefaultRequestBuilder(super.getContext());
        this.handlerExecuteHandlerRegistry = new HandlerExecutionResultHandlerRegistry();
        this.handlerExecuteResultResolverRegistry = new ExecutionResultResolverRegistry();

        this.executionMethodHandlerRegistry = new ExecutionMethodResultHandlerRegistry();
        this.executionMethodResultResolverRegistry = new ExecutionResultResolverRegistry();
        this.executionMethodResultResolverRegistry.registExecutionResultResolver(new SimpleExecutionResultResolver());
    }

    @Override
    public ExecutionResult<T> execute(T t) {
        try {
            return super.execute(t);
        } catch (IOException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ExecutionResult<T> doExecute(T requestEntity) throws IOException, ExecutionException {
        ExecutionResult<T> executionResult = new ExecutionResult<>(requestEntity);

        logger.info("+ begin execute request "+requestEntity);

        Request request = requestBuilder.buildRequest(requestEntity);
        if(request==null){
            logger.error("request build error, no url parsed out");
        }
        requestEntity.setRequest(request);
        logger.debug("build request successfully!");

        Object executionHandler = requestEntity.getExecutionHandler();
        //如果有ExecutionMethod 则不调用Executor执行，在ExecutionMethod内传入Executor参数来执行
        if(executionHandler!=null && executionHandler instanceof Method){
            executeByExecutionMethod(executionResult);
        }else{
            //先调用beforeHandler 用来判断是否继续执行，执行完后再调用afterHandler 获取执行结果
            executeByExecutionHandler(executionResult);
        }

        if(executionResult.isFailed()){
            logger.error(requestEntity+" 执行失败");
            //TODO 失败处理器
        }

        //执行结束处理IntervalStrategy
        executeInterval(executionResult,requestEntity);

        executionResult.setExecuted(true);
        logger.info("- end execute request"+requestEntity+"\n");
        return executionResult;
    }

    private void executeInterval(ExecutionResult<T> executionResult,T requestEntity) {
        if(executionResult.getAfterResult() instanceof IntervalAction){
            IntervalAction afterResult = (IntervalAction) executionResult.getAfterResult();
            try {
                int interval = afterResult.getInterval();
                logger.info(interval+"ms interval of "+requestEntity);
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeByExecutionMethod(ExecutionResult<T> executionResult) throws ExecutionException {
        ExecutionContext context = super.getContext();
        getExecutionMethodHandlerRegistry().invokeBeforeExecutionHandler(executionResult, context);
        getExecutionMethodResultResolverRegistry().beforeExecuteResolve(executionResult, context);

        T requestEntity = executionResult.getOrginalSeq();

        HandlerMethodWrapper executionMethodWrapper = bulidExecutionHandlerMethodWrapper(requestEntity);
        Object executionMethodResult = executionMethodWrapper.invokeHandlerMethod();
        executionResult.setAfterResult(executionMethodResult);

        getExecutionMethodHandlerRegistry().invokeAfterExecutionHandler(executionResult, context, Collections.singletonList(RequestHandlerMethodExecutionResultHandler.class));
        getExecutionMethodResultResolverRegistry().afterExecuteResolve(executionResult, context);
    }

    private void executeByExecutionHandler(ExecutionResult<T> executionResult) throws ExecutionException, IOException {
        ExecutionContext context = super.getContext();
        //TODO 执行handlerMethodRequestExecutionHandler之前，检查其属性resultResolver，如果不是SimpleExecutionResultResolver则使其变成该Resolver
        getHandlerExecuteHandlerRegistry().invokeBeforeExecutionHandler(executionResult, context);
        getHandlerExecuteResultResolverRegistry().beforeExecuteResolve(executionResult, context);

        T requestEntity = executionResult.getOrginalSeq();
        if(skipExecuteCondition(executionResult)){
            logger.warn("request "+requestEntity.getFullName()+" execution skiped");
            return;
        }
        HttpResponse response = exeucteRequest(requestEntity);
        executionResult.setHttpResponse(response);
        getHandlerExecuteHandlerRegistry().invokeAfterExecutionHandler(executionResult, context);
        getHandlerExecuteResultResolverRegistry().afterExecuteResolve(executionResult, context);
    }

    private HandlerMethodWrapper bulidExecutionHandlerMethodWrapper(T requestEntity) {
        HandlerMethodWrapper handlerMethodWrapper = new HandlerMethodWrapper();
        Method method = (Method) requestEntity.getExecutionHandler();
        handlerMethodWrapper.setMethod(method);
        Object[] args = new ExecutionMethodArgumentInfo(super.getContext()).fetchExecutionMethodArgs(method, requestEntity.getRequest());
        handlerMethodWrapper.setArgs(args);
        RequestChainEntity parent = requestEntity.getParent();
        Assert.notNull(parent,"request parent can't be null");
        Object currentChainInstance = parent.getChainInstance();
        handlerMethodWrapper.setInvokeInstance(currentChainInstance);
        return handlerMethodWrapper;
    }

    private boolean skipExecuteCondition(ExecutionResult<T> executionResult) {
        return executionResult.isSkipExecute();
    }

    public HttpResponse exeucteRequest(RequestEntity requestEntity) throws ExecutionException, IOException {
        Request request = requestEntity.getRequest();
        Executor executor = super.getContext().getExecutor();

        if (requestEntity.getRequestLogInfo().isShowUrl()) {
            logger.info("requesting <"+request.toString()+">");
        }
        logger.debug("executing request ["+request+"]");

        HttpResponse response = executor.execute(request).returnResponse();

        ResponseLogInfo responseLogInfo = requestEntity.getResponseLogInfo();
        if (responseLogInfo.isShowStatus()) {
            logger.info(requestEntity.getName()+" response status:"+response.getStatusLine());
        }

        if(responseLogInfo.isShowResult()){
            logger.info(requestEntity.getName()+" response result:\n"+EntityUtils.toString(response.getEntity()));
        }

        if(responseLogInfo.isShowSetCookies()){
            Header[] headers = response.getHeaders("Set-Cookie");
            for (Header header : headers) {
                logger.info("set cookie < "+header+" >");
            }
        }
        return response;
    }

    public HttpRequestBuilder getRequestBuilder() {
        return requestBuilder;
    }

    public void setRequestBuilder(HttpRequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public RequestExecutionResultHandlerRegistry getHandlerExecuteHandlerRegistry() {
        return handlerExecuteHandlerRegistry;
    }

    public ExecutionResultResolverRegistry getHandlerExecuteResultResolverRegistry() {
        return handlerExecuteResultResolverRegistry;
    }

    public ExecutionResultResolverRegistry getExecutionMethodResultResolverRegistry() {
        return executionMethodResultResolverRegistry;
    }

    public RequestExecutionResultHandlerRegistry getExecutionMethodHandlerRegistry() {
        return executionMethodHandlerRegistry;
    }

}

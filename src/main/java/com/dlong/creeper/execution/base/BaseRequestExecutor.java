package com.dlong.creeper.execution.base;

import com.dlong.creeper.control.IntervalAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.handler.RequestHandlerMethodExecutionResultHandler;
import com.dlong.creeper.execution.handler.info.ExecutionMethodArgumentInfo;
import com.dlong.creeper.execution.registry.ExecutionMethodResultHandlerRegistry;
import com.dlong.creeper.execution.registry.HandlerExecutionResultHandlerRegistry;
import com.dlong.creeper.execution.registry.base.ExecutionResultResolverRegistry;
import com.dlong.creeper.execution.registry.base.RequestExecutionResultHandlerRegistry;
import com.dlong.creeper.execution.request.DefaultRequestBuilder;
import com.dlong.creeper.execution.request.HttpRequestBuilder;
import com.dlong.creeper.execution.resolver.SimpleExecutionResultResolver;
import com.dlong.creeper.model.HandlerMethodWrapper;
import com.dlong.creeper.model.log.ResponseLogInfo;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.ContentResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;


public class BaseRequestExecutor<T extends RequestEntity> extends AbstractLoopableExecutor<T> implements RequestExecutor<T> {
    private HttpRequestBuilder requestBuilder;

    private RequestExecutionResultHandlerRegistry handlerExecuteHandlerRegistry;
    private ExecutionResultResolverRegistry handlerExecuteResultResolverRegistry;

    private RequestExecutionResultHandlerRegistry executionMethodHandlerRegistry;
    private ExecutionResultResolverRegistry executionMethodResultResolverRegistry;

    private static Logger logger= Logger.getLogger(BaseRequestExecutor.class);

    public BaseRequestExecutor(ChainContext context) {
        this(context,false);
    }

    public BaseRequestExecutor(ChainContext context, boolean isMultiThread) {
        super(context, isMultiThread);
        init();
    }

    public BaseRequestExecutor(ChainContext context, HttpRequestBuilder requestBuilder) {
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

        Request request;
        try {
            request = requestBuilder.buildRequest(requestEntity.getRequestInfo());
        } catch (Exception e) {
            logger.error("request build error, no url parsed out");
            return handleError(e,executionResult);
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

    private ExecutionResult<T> handleError(Exception e,ExecutionResult<T> executionResult) {
        logger.error(e.getClass().getSimpleName()+":"+e.getMessage());
        executionResult.setFailed(true);
        executionResult.setException(e);
        return executionResult;
    }

    private void executeInterval(ExecutionResult<T> executionResult, T requestEntity) {
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
        ChainContext context = super.getContext();
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
        ChainContext context = super.getContext();
        //TODO 执行handlerMethodRequestExecutionHandler之前，检查其属性resultResolver，如果不是SimpleExecutionResultResolver则使其变成该Resolver
        getHandlerExecuteHandlerRegistry().invokeBeforeExecutionHandler(executionResult, context);
        getHandlerExecuteResultResolverRegistry().beforeExecuteResolve(executionResult, context);

        T requestEntity = executionResult.getOrginalSeq();
        if(skipExecuteCondition(executionResult)){
            logger.warn("request "+requestEntity.getFullName()+" execution skiped");
            return;
        }
        try {
            exeucteRequest(requestEntity,executionResult);
        } catch (Exception e) {
            handleError(e,executionResult);
        }
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

    public ExecutionResult<T> exeucteRequest(RequestEntity requestEntity, ExecutionResult<T> executionResult) throws ExecutionException, IOException {
        Request request = requestEntity.getRequest();
        Executor executor = super.getContext().getExecutor();

        if (requestEntity.getRequestLogInfo().isShowUrl()) {
            logger.info("requesting <"+request.toString()+">");
        }
        logger.debug("executing request ["+request+"]");

        Response response = executor.execute(request);
        HttpResponse httpResponse = response.returnResponse();

        ResponseLogInfo responseLogInfo = requestEntity.getResponseLogInfo();
        if (responseLogInfo.isShowStatus()) {
            logger.info(requestEntity.getName()+" response status:"+httpResponse.getStatusLine());
        }

        if(responseLogInfo.isShowResult()){
            logger.info(requestEntity.getName()+" response result:\n"+ EntityUtils.toString(httpResponse.getEntity()));
        }

        if(responseLogInfo.isShowSetCookies()){
            Header[] headers = httpResponse.getHeaders("Set-Cookie");
            for (Header header : headers) {
                logger.info("set cookie < "+header+" >");
            }
        }

        executionResult.setHttpResponse(httpResponse);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode<300 || statusCode>400){
            executionResult.setContent(new ContentResponseHandler().handleResponse(httpResponse));
        }
        return executionResult;
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

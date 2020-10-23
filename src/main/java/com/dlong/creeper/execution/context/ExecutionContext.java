package com.dlong.creeper.execution.context;

import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.request.DefaultRequestBuilder;
import com.dlong.creeper.execution.request.HttpRequestBuilder;
import com.dlong.creeper.expression.ContextExpressionParser;
import com.dlong.creeper.util.SSLUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;

public class ExecutionContext {
    protected Executor executor;
    protected FormParamStore paramStore;
    protected CookieStore cookieStore;
    protected ContextParamStore contextStore;
    protected HttpRequestBuilder requestBuilder;

    public ExecutionContext() {
        this(null,null);
    }

    public ExecutionContext(ContextParamStore contextStore) {
        this.cookieStore = new BasicCookieStore();
        this.paramStore = new FormParamStore();
        this.contextStore = contextStore;
        init(executor);
    }

    public ExecutionContext(ExecutionContext context) {
      this(null,context);
    }

    public ExecutionContext(Executor executor) {
        this(executor,null);
    }

    public ExecutionContext(Executor executor,ExecutionContext context) {
        if (context!=null){
            this.cookieStore = context.getCookieStore();
            this.contextStore = context.getContextStore();
            this.paramStore = context.getParamStore();
        }else{
            this.cookieStore = new BasicCookieStore();
            this.contextStore = new ContextParamStore();
            this.paramStore = new FormParamStore();
        }
        init(executor);
    }

    protected void init(Executor executor) {
        try {
            if(executor == null){
                this.executor = createExecutor();
            }else{
                this.executor = executor;
            }
        } catch (Exception e) {
            throw new RuntimeExecuteException(e);
        }
        setExecutor(this.executor);
        this.requestBuilder=new DefaultRequestBuilder(this);
    }

    private Executor createExecutor() {
        return Executor.newInstance(SSLUtil.getIgnoreVerifySSLHttpClient());
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
        this.executor.use(cookieStore);
    }

    public FormParamStore getParamStore() {
        return paramStore;
    }

    public void setParamStore(FormParamStore paramStore) {
        this.paramStore = paramStore;
    }

    public ContextParamStore getContextStore() {
        return this.contextStore;
    }

    public void setContextStore(ContextParamStore contextStore) {
        this.contextStore = contextStore;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        if(this.executor == null){
            this.executor = createExecutor();
        }
        this.cookieStore = cookieStore;
        this.executor.use(this.cookieStore);
    }

    public ContextExpressionParser getExpressionParser() {
        return this.contextStore.getExpressionParser();
    }

    @Override
    public ExecutionContext clone() throws CloneNotSupportedException {
        ExecutionContext clone = (ExecutionContext) super.clone();
        clone.setContextStore(this.contextStore.clone());
        return clone;
    }

    public HttpRequestBuilder getRequestBuilder() {
        return requestBuilder;
    }

    public void setRequestBuilder(HttpRequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }
}

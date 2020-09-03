package com.dlong.creeper.resolver;

import com.dlong.creeper.exception.RuntimeResolveException;
import com.dlong.creeper.exception.UnsupportedFluentReturnTypeException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;
import com.dlong.creeper.execution.request.DefaultRequestBuilder;
import com.dlong.creeper.execution.request.HttpRequestBuilder;
import com.dlong.creeper.model.Param;
import com.dlong.creeper.model.seq.RequestInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FluentRequestInvocationHandler implements InvocationHandler{
    private Logger logger = Logger.getLogger(FluentRequestInvocationHandler.class);

    private ExecutionContext context;
    private HttpRequestBuilder requestBuilder;
    private Map<AnnotatedElement, RequestInfo> requestInfoMap;

    private static final List<Class<?>> AFTER_EXECUTE_RETURN_TYPES = Arrays.asList(HttpResponse.class, Content.class, String.class, InputStream.class, Response.class);
    private static final Class<com.dlong.creeper.annotation.Parameter> PARAMETER_ANNO_CLASS = com.dlong.creeper.annotation.Parameter.class;

    public FluentRequestInvocationHandler(Class<?> mappingClass, ExecutionContext context) {
        if(context != null){
            this.context = context;
        }else{
            this.context = new ExecutionContext();
        }
        this.requestInfoMap = new DefaultRequestInfoResolver().resolve(mappingClass);
        this.requestBuilder = new DefaultRequestBuilder(context.getContextStore(), new FormParamStore());
    }

    public FluentRequestInvocationHandler(Class<?> mappingClass) {
        this(mappingClass,null);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestInfo requestInfo = requestInfoMap.get(method);
        //参数值为$NULL的全部设为空字符
        setNullParamsAsEmpty(requestInfo);

        //解析method被注解Parameter的参数，参数值将作为Http参数
        addArgsParamsToStore(method,args);

        Class<?> returnType = method.getReturnType();
        Request request = this.requestBuilder.buildRequest(requestInfo);
        Object result;
        if(returnType.equals(Request.class)){
            result = request;
        }else if(AFTER_EXECUTE_RETURN_TYPES.contains(returnType)){
            Response response = executeRequest(request);
            if(returnType.equals(String.class)){
                result = response.returnContent().asString();
            }else if(returnType.equals(HttpResponse.class)){
                result = response.returnResponse();
            }else if(returnType.equals(Content.class)){
                result = response.returnContent();
            }else if(returnType.equals(InputStream.class)){
                result = response.returnContent().asStream();
            }else{
                result = response;
            }
        }else if(returnType.getSimpleName().equals("byte[]")){
            result = executeRequest(request).returnContent().asBytes();
        }else{
            throw new UnsupportedFluentReturnTypeException("please use these types as return type. [org.apache.http.client.fluent.Request.class, org.apache.http.HttpResponse.class, String.class, InputStream.class, Response.class]");
        }
        return result;
    }

    private Response executeRequest(Request request) throws IOException {
        Response response = context.getExecutor().execute(request);
        logger.info("request is executed in method!\n");
        return response;
    }

    private void setNullParamsAsEmpty(RequestInfo requestInfo) {
        List<Param> params = requestInfo.getParams();
        List<Param> res=new ArrayList<>(params.size());
        for (Param param : params) {
            if (param.getValue().equals(FormParamStore.NULL_VALUE)) {
                res.add(new Param(param.getName(),""));
            }else{
                res.add(param);
            }
        }
        requestInfo.setParams(res);
    }

    private void addArgsParamsToStore(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            com.dlong.creeper.annotation.Parameter parameterAnnotation = parameter.getAnnotation(PARAMETER_ANNO_CLASS);
            if(parameterAnnotation!=null){
                String paramName = parameterAnnotation.name();

                //获取参数值
                Object argVal = args[i];
                if(argVal == null){
                    //如果参数值为空，则使用注解的值
                    String defaultVal = parameterAnnotation.value();
                    if(!defaultVal.equals(FormParamStore.NULL_VALUE)){
                        argVal = defaultVal;
                    }else{
                        //若注解也为空，则使用空字符
                        argVal = "";
                    }
                }
                context.getParamStore().addParam(paramName,argVal.toString());
            }else{
                throw new RuntimeResolveException("parameter "+parameter.getName()+" of method "+method.getName()+" does't annotated with @Parameter");
            }
        }
    }
}

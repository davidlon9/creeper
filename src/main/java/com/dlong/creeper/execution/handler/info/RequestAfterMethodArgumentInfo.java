package com.dlong.creeper.execution.handler.info;

import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.result.ExecutionResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class RequestAfterMethodArgumentInfo extends HandlerMethodArgumentInfo {
    private static Logger logger= Logger.getLogger(RequestAfterMethodArgumentInfo.class);
    public static final List<Class<?>> RESPONSE_DATA_TYPES = Arrays.asList(
            HttpResponse.class, Content.class, String.class, InputStream.class);

    static {
        supportedTypeList.addAll(RESPONSE_DATA_TYPES);
        supportedTypeList.add(Exception.class);
        supportedTypeList.add(ExecutionResult.class);
    }

    public RequestAfterMethodArgumentInfo(ExecutionContext context) {
        super(context);
    }

    public Object[] fetchHandlerMethodArgs(Method method, ExecutionResult result) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args=new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if(!isSupportArgType(parameterType)){
                args[i]=null;
                logger.warn(parameterType +" can't as a arg for handler entity method,this arg value will be null. please try these parameter types:"+getSupportedTypeList().toString());
                continue;
            }
            try {
                if (isResponseDataType(parameterType)) {
                    args[i] = getData(result, parameterType);
                }else if(parameterType.getSimpleName().equals("byte[]") && result.getContent()!=null){
                    args[i] = result.getContent().asBytes();
                }else if(parameterType.equals(ExecutionResult.class)){
                    args[i] = result;
                }else if(parameterType.equals(Exception.class)){
                    args[i] = result.getException();
                }else{
                    args[i] = getArgInstance(parameterType);
                }
            } catch (IOException e) {
                throw new RuntimeExecuteException(e);
            }
        }
        return args;
    }

    public static boolean isResponseDataType(Class<?> type) {
        return RESPONSE_DATA_TYPES.contains(type);
    }

    public static Object getData(ExecutionResult executionResult, Class<?> dataType) throws IOException {
        Object result = null;
        Content content = executionResult.getContent();
        if(content!=null){
            if(dataType.equals(String.class)){
                result = content.asString();
            }else if(dataType.equals(Content.class)){
                result = content;
            }else if(dataType.equals(InputStream.class)){
                result = content.asStream();
            }
        }else if(dataType.equals(HttpResponse.class)){
            result = executionResult.getHttpResponse();
        }else{
            logger.warn(dataType+" type arg value is null");
        }
        return result;
    }
}

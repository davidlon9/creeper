package com.dlong.creeper.execution.handler.info;

import com.dlong.creeper.execution.context.ExecutionContext;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

public class RequestBeforeMethodArgumentInfo extends HandlerMethodArgumentInfo {
    private static Logger logger=Logger.getLogger(RequestBeforeMethodArgumentInfo.class);

    static {
        supportedTypeList.add(Request.class);
    }

    public RequestBeforeMethodArgumentInfo(ExecutionContext context) {
        super(context);
    }

    public Object[] fetchHandlerMethodArgs(Method method, Request request) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args=new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if(!isSupportArgType(parameterType)){
                args[i] = findValueInContext(getParameterName(method, i), parameterType);
                continue;
            }
            if (parameterType.equals(Request.class)) {
                args[i] = request;
            }else{
                args[i] = getArgInstance(parameterType);
            }
        }
        return args;
    }
}

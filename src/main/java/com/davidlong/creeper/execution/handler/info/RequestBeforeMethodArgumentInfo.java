package com.davidlong.creeper.execution.handler.info;

import com.davidlong.creeper.execution.context.ExecutionContext;
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
            if(!isSupportArgType(parameterTypes[i])){
                args[i]=null;
                logger.warn(parameterTypes[i]+" can't as a arg for handler entityTarget,this arg value will be null. please try these parameter types:"+getSupportedTypeList().toString());
                continue;
            }
            if (parameterTypes[i].equals(Request.class)) {
                args[i] = request;
            }else{
                args[i] = getArgInstance(parameterTypes[i]);
            }
        }
        return args;
    }
}

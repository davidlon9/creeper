package com.dlong.creeper.execution.handler.info;

import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.context.ExecutionContext;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;

public class RequestAfterMethodArgumentInfo extends HandlerMethodArgumentInfo {
    private static Logger logger=Logger.getLogger(RequestAfterMethodArgumentInfo.class);

    static {
        supportedTypeList.add(HttpResponse.class);
        supportedTypeList.add(String.class);
    }

    public RequestAfterMethodArgumentInfo(ExecutionContext context) {
        super(context);
    }

    public Object[] fetchHandlerMethodArgs(Method method, HttpResponse response) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args=new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if(!isSupportArgType(parameterTypes[i])){
                args[i]=null;
                logger.warn(parameterTypes[i]+" can't as a arg for handler entity method,this arg value will be null. please try these parameter types:"+getSupportedTypeList().toString());
                continue;
            }
            try {
                if (parameterTypes[i].equals(HttpResponse.class)) {
                    args[i] = response;
                }else if(parameterTypes[i].equals(String.class)){
                    args[i] = EntityUtils.toString(response.getEntity());
                }else{
                    args[i] = getArgInstance(parameterTypes[i]);
                }
            } catch (IOException e) {
                throw new RuntimeExecuteException(e);
            }
        }
        return args;
    }

}

package com.dlong.creeper.execution.handler.info;

import com.dlong.creeper.execution.context.ExecutionContext;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

public class ExecutionMethodArgumentInfo extends HandlerMethodArgumentInfo {
    private static Logger logger=Logger.getLogger(ExecutionMethodArgumentInfo.class);

    static {
        supportedTypeList.add(Request.class);
        supportedTypeList.add(Executor.class);
    }

    public ExecutionMethodArgumentInfo(ExecutionContext context) {
        super(context);
    }

    @Override
    public void initContextArgInstanceMap(ExecutionContext context) {
        super.initContextArgInstanceMap(context);
        getContextArgTypeInstanceMap().put(Executor.class,context.getExecutor());
    }

    public Object[] fetchExecutionMethodArgs(Method method,Request request) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args=new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if(!isSupportArgType(parameterTypes[i])){
                args[i]=null;
                logger.warn(parameterTypes[i]+" can't as a arg for handler entity method,this arg value will be null. please try these parameter types:"+getSupportedTypeList().toString());
                continue;
            }

            if (parameterTypes[i].equals(Request.class)) {
                args[i]=request;
            }else{
                args[i] = getArgInstance(parameterTypes[i]);
            }
        }
        return args;
    }
}

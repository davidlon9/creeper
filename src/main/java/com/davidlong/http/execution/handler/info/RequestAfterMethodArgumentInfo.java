package com.davidlong.http.execution.handler.info;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.util.ResultUtil;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;

public class RequestAfterMethodArgumentInfo extends HandlerMethodArgumentInfo {
    private static Logger logger=Logger.getLogger(RequestAfterMethodArgumentInfo.class);

    static {
        supportedTypeList.add(HttpResponse.class);
        supportedTypeList.add(JSONObject.class);
        supportedTypeList.add(Map.class);
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
                logger.warn(parameterTypes[i]+" can't as a arg for handler entityTarget,this arg value will be null. please try these parameter types:"+getSupportedTypeList().toString());
                continue;
            }
            if (parameterTypes[i].equals(HttpResponse.class)) {
                args[i] = response;
            }else if(Map.class.isAssignableFrom(parameterTypes[i])){
                args[i] = ResultUtil.getResult(response);
            }else{
                args[i] = getArgInstance(parameterTypes[i]);
            }
        }
        return args;
    }

}

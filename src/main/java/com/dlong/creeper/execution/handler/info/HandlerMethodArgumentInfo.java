package com.dlong.creeper.execution.handler.info;

import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;
import com.dlong.creeper.execution.context.ParamStore;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerMethodArgumentInfo {
    private static Logger logger=Logger.getLogger(HandlerMethodArgumentInfo.class);

    private final Map<Class,Object> contextArgTypeInstanceMap = new HashMap<>();

    protected static final List<Class> supportedTypeList = new ArrayList<>();

    static{
        supportedTypeList.add(ExecutionContext.class);
        supportedTypeList.add(FormParamStore.class);
        supportedTypeList.add(ContextParamStore.class);
        supportedTypeList.add(BasicCookieStore.class);
        supportedTypeList.add(CookieStore.class);
        supportedTypeList.add(ParamStore.class);
    }

    public HandlerMethodArgumentInfo(ExecutionContext context) {
        initContextArgInstanceMap(context);
    }

    public void initContextArgInstanceMap(ExecutionContext context){
        contextArgTypeInstanceMap.put(ExecutionContext.class,context);
        contextArgTypeInstanceMap.put(FormParamStore.class,context.getParamStore());
        contextArgTypeInstanceMap.put(ContextParamStore.class,context.getContextStore());
        contextArgTypeInstanceMap.put(BasicCookieStore.class,context.getCookieStore());
        contextArgTypeInstanceMap.put(CookieStore.class,context.getCookieStore());
        contextArgTypeInstanceMap.put(ParamStore.class,context.getParamStore());
    }

    public Object getArgInstance(Class clz){
        return contextArgTypeInstanceMap.get(clz);
    }

    public boolean isSupportArgType(Class clz){
        return supportedTypeList.contains(clz);
    }

    public Map<Class, Object> getContextArgTypeInstanceMap() {
        return contextArgTypeInstanceMap;
    }

    public static List<Class> getSupportedTypeList() {
        return supportedTypeList;
    }

    public Object[] fetchHandlerMethodArgs(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args=new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if(!isSupportArgType(parameterTypes[i])){
                args[i]=null;
                logger.warn(parameterTypes[i]+" can't as a arg for handler entity method,this arg value will be null. please try these parameter types:"+getSupportedTypeList().toString());
                continue;
            }
            args[i] = getArgInstance(parameterTypes[i]);
        }
        return args;
    }

//    /**
//     * 获取前处理器方法的参数
//     * @param entityTarget
//     * @param request
//     * @return
//     */
//    private Object[] getBeforeHandlerArgs(Method entityTarget, Request request) {
//        Class<?>[] parameterTypes = entityTarget.getParameterTypes();
//        Object[] args=new Object[parameterTypes.length];
//        for (int i = 0; i < parameterTypes.length; i++) {
//            if (parameterTypes[i].equals(Request.class)) {
//                args[i]=request;
//            }else if(parameterTypes[i].equals(ExecutionContext.class)){
//                args[i]=super.getContext();
//            }else if(parameterTypes[i].equals(FormParamStore.class)){
//                args[i]=super.getContext().getParamStore();
//            }else if(parameterTypes[i].equals(ContextParamStore.class)){
//                args[i]=super.getContext().getContextStore();
//            }else if(parameterTypes[i].equals(BasicCookieStore.class)){
//                args[i]=super.getContext().getCookieStore();
//            }else if(parameterTypes[i].isInterface()){
//                if(ParamStore.class.isAssignableFrom(parameterTypes[i])){
//                    args[i]=super.getContext().getParamStore();
//                }else if(CookieStore.class.isAssignableFrom(parameterTypes[i])){
//                    args[i]=super.getContext().getCookieStore();
//                }else{
//                    args[i]=null;
//                }
//            }else{
//                args[i]=null;
//            }
//            if(args[i]==null){
//                logger.warn(parameterTypes[i]+" can't as parameter,,this param jsonKey will be null. please try these parameter types:Request, ExecutionContext, ParamStore, CookieStore");
//            }
//        }
//        return args;
//    }
//
//    /**
//     * 获取后处理器方法的参数
//     * @param entityTarget
//     * @param response
//     * @return
//     */
//    private Object[] getAfterHandlerArgs(Method entityTarget, HttpResponse response) {
//        Class<?>[] parameterTypes = entityTarget.getParameterTypes();
//        Object[] args=new Object[parameterTypes.length];
//        for (int i = 0; i < parameterTypes.length; i++) {
//            if (parameterTypes[i].equals(HttpResponse.class)) {
//                args[i]=response;
//            }else if(parameterTypes[i].equals(ExecutionContext.class)){
//                args[i]=super.getContext();
//            }else if(parameterTypes[i].equals(FormParamStore.class)){
//                args[i]=super.getContext().getParamStore();
//            }else if(parameterTypes[i].equals(ContextParamStore.class)){
//                args[i]=super.getContext().getContextStore();
//            }else if(parameterTypes[i].equals(BasicCookieStore.class)){
//                args[i]=super.getContext().getCookieStore();
//            }else if(parameterTypes[i].equals(JSONObject.class)){
//                args[i]= ResultUtil.getResult(response);
//            }else if(parameterTypes[i].isInterface()){
//                if(ParamStore.class.isAssignableFrom(parameterTypes[i])){
//                    args[i]=super.getContext().getParamStore();
//                }else if(CookieStore.class.isAssignableFrom(parameterTypes[i])){
//                    args[i]=super.getContext().getCookieStore();
//                }else if(Map.class.isAssignableFrom(parameterTypes[i])){
//                    args[i]=ResultUtil.getResult(response);
//                }else{
//                    args[i]=null;
//                }
//            }else{
//                args[i]=null;
//            }
//            if(args[i]==null){
//                logger.warn(parameterTypes[i]+" can't as parameter, please try these parameter types:HttpResponse, ExecutionContext, ParamStore, CookieStore, JSONObject");
//            }
//        }
//        return args;
//    }
}

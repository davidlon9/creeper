package com.dlong.creeper.model;

import com.dlong.creeper.exception.RuntimeExecuteException;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMethodWrapper{
    private Object[] args;

    private Object invokeInstance;

    private Method method;

    private static Logger logger = Logger.getLogger(HandlerMethodWrapper.class);

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getInvokeInstance() {
        return invokeInstance;
    }

    public void setInvokeInstance(Object invokeInstance) {
        this.invokeInstance = invokeInstance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getArgument(int index){
        return args.length>index ? args[index] : null;
    }

    public <E> E  getArgument(Class<E> type){
        if(args==null){
            return null;
        }
        for (Object arg : args) {
            if (type.isAssignableFrom(arg.getClass())) {
                return (E) arg;
            }
        }
        return null;
    }

    public Object invokeHandlerMethod(){
        Object result=null;
        try {
            if(method!=null){
                result = method.invoke(this.invokeInstance, this.args);
            }else{
                logger.warn("handler method is null");
            }
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e ) {
            throw new RuntimeExecuteException(" HandlerMethod "+method.getName()+" invoke failed",e.getCause());
        }
        return result;
    }

}

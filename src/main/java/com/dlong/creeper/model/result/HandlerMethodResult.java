package com.dlong.creeper.model.result;

public class HandlerMethodResult {
    private HandlerMethodType methodType;

    private Class resultType;

    private Object result;

    public HandlerMethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(HandlerMethodType methodType) {
        this.methodType = methodType;
    }

    public Class getResultType() {
        return resultType;
    }

    public void setResultType(Class resultType) {
        this.resultType = resultType;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

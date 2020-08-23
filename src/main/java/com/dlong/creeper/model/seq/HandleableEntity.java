package com.dlong.creeper.model.seq;

import com.dlong.creeper.execution.handler.entity.SequentialHandler;

public class HandleableEntity extends SequentialEntity{
    private Object afterHandler;
    private Object beforeHandler;
    private Object executionHandler;

    public Object getAfterHandler() {
        return afterHandler;
    }

    public void setAfterHandler(Object afterHandler) {
        this.afterHandler = afterHandler;
    }

    public Object getBeforeHandler() {
        return beforeHandler;
    }

    public void setBeforeHandler(Object beforeHandler) {
        this.beforeHandler = beforeHandler;
    }

    public Object getExecutionHandler() {
        return executionHandler;
    }

    public void setExecutionHandler(Object executionHandler) {
        this.executionHandler = executionHandler;
    }

    public void setExecutionHandler(SequentialHandler handler) {
        this.executionHandler = handler;
    }

    public void setAfterHandler(SequentialHandler handler) {
        this.afterHandler = handler;
    }

    public void setBeforeHandler(SequentialHandler handler) {
        this.beforeHandler = handler;
    }
}

package com.davidlong.http.model;

import com.davidlong.http.model.seq.SequentialEntity;
import org.apache.http.client.fluent.Request;

public class ExecutionInputParam<T extends SequentialEntity>{
    private T entity;

    public ExecutionInputParam() {
    }

    public ExecutionInputParam(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}

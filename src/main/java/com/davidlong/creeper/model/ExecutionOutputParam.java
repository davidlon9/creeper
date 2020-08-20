package com.davidlong.http.model;

import com.davidlong.http.model.seq.SequentialEntity;
import org.apache.http.HttpResponse;

public class ExecutionOutputParam<T extends SequentialEntity> {
    private T entity;

    public ExecutionOutputParam() {
    }

    public ExecutionOutputParam(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

}

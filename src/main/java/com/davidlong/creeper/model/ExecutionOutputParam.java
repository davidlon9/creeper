package com.davidlong.creeper.model;

import com.davidlong.creeper.model.seq.SequentialEntity;

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

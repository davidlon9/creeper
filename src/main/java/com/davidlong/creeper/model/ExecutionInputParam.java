package com.davidlong.creeper.model;

import com.davidlong.creeper.model.seq.SequentialEntity;

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

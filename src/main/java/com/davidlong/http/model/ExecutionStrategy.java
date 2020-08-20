package com.davidlong.http.model;

public enum ExecutionStrategy {
    Parallel(1),
    Sequential(2);

    private int code;

    ExecutionStrategy(int code) {
        this.code=code;
    }
}

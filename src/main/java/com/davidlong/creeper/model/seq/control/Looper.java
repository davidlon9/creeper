package com.davidlong.creeper.model.seq.control;

import com.davidlong.creeper.annotation.control.ExecutionMode;

public class Looper {
    private ExecutionMode executionMode;

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }
}

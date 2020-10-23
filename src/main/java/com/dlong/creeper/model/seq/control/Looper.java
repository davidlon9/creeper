package com.dlong.creeper.model.seq.control;

import com.dlong.creeper.annotation.control.ExecutionMode;

public class Looper {
    private Spliter spliter;
    private ExecutionMode executionMode;

    public Spliter getSpliter() {
        return spliter;
    }

    public void setSpliter(Spliter spliter) {
        this.spliter = spliter;
    }

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }
}

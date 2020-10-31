package com.dlong.creeper.model.seq.control;

import com.dlong.creeper.execution.spliter.SpliterHandler;

public class Spliter {
    private int splitSize = 1;
    private int maxPoolSize = 1;
    private int maxRetryNum = 0;
    private SpliterHandler handler;

    public int getSplitSize() {
        return splitSize;
    }

    public void setSplitSize(int splitSize) {
        this.splitSize = splitSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMaxRetryNum() {
        return maxRetryNum;
    }

    public void setMaxRetryNum(int maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    public SpliterHandler getHandler() {
        return handler;
    }

    public void setHandler(SpliterHandler handler) {
        this.handler = handler;
    }
}

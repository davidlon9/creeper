package com.davidlong.http.model.seq.multi;

public class MultiRequestQueueEntity extends MultiRequestEntity {
    private String queueContextKey;
    private String queueElementKey;
    private String stopConditionExpr;
    private int delay;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getQueueContextKey() {
        return queueContextKey;
    }

    public void setQueueContextKey(String queueContextKey) {
        this.queueContextKey = queueContextKey;
    }

    public String getQueueElementKey() {
        return queueElementKey;
    }

    public void setQueueElementKey(String queueElementKey) {
        this.queueElementKey = queueElementKey;
    }

    public String getStopConditionExpr() {
        return stopConditionExpr;
    }

    public void setStopConditionExpr(String stopConditionExpr) {
        this.stopConditionExpr = stopConditionExpr;
    }
}

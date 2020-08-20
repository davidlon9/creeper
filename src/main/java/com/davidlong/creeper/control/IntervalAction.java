package com.davidlong.http.control;

public abstract class IntervalAction extends AutoCurrentIndexAction {
    private int interval;

    protected IntervalAction(String name) {
        super(name);
    }

    public IntervalAction(String name, int interval) {
        super(name);
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}

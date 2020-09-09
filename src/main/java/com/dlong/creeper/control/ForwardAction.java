package com.dlong.creeper.control;

import org.springframework.util.Assert;

public class ForwardAction extends IntervalAction {
    public ForwardAction() {
        super("FORWARD");
    }

    public ForwardAction(int interval) {
        super("FORWARD", interval);
    }

    @Override
    public Integer nextSequential() {
        Assert.notNull(current);
        return ++current;
    }
}

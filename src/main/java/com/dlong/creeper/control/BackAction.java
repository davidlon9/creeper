package com.dlong.creeper.control;

import org.springframework.util.Assert;

public class BackAction extends IntervalAction {

    BackAction() {
        super("BACK");
    }

    public BackAction(int interval) {
        super("BACK",interval);
    }

    @Override
    public Integer nextSequential() {
        Assert.notNull(current);
        return --current;
    }

}

package com.davidlong.creeper.control;

import org.springframework.util.Assert;

public class ForwardAction extends AutoCurrentIndexAction {
    public ForwardAction() {
        super("FORWARD");
    }

    @Override
    public Integer nextSequential() {
        Assert.notNull(current);
        return ++current;
    }
}

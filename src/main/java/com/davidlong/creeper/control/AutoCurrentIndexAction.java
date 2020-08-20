package com.davidlong.creeper.control;

import org.springframework.util.Assert;

public abstract class AutoCurrentIndexAction extends AbstractAction {
    protected Integer current;

    protected AutoCurrentIndexAction(String name) {
        super(name);
    }

    public Integer getCurrent() {
        Assert.notNull(current,"current index is null");
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }
}



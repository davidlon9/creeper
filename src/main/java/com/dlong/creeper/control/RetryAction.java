package com.dlong.creeper.control;

import org.springframework.util.Assert;

public class RetryAction extends IntervalAction {
    RetryAction() {
        super("RETRY");
    }

    public RetryAction(int interval) {
        super("RETRY",interval);
    }

    @Override
    public Integer nextSequential() {
        Assert.notNull(current);
        return current;
    }

    @Override
    public int hashCode() {
        return 31*super.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        } else if (!(obj instanceof RetryAction)) {
            return false;
        } else {
            RetryAction that = (RetryAction) obj;
            return this.getName().equals(that.getName());
        }
    }
}

package com.dlong.creeper.control;

public class ContinueAction extends IntervalAction {

    public ContinueAction(int interval) {
        super("CONTINUE",interval);
    }

    @Override
    public Object nextSequential() {
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
        } else if (!(obj instanceof ContinueAction)) {
            return false;
        } else {
            ContinueAction that = (ContinueAction) obj;
            return this.getName().equals(that.getName());
        }
    }

}

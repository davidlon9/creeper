package com.davidlong.http.control;

public class StopAllAction extends AutoCurrentIndexAction {
    public StopAllAction() {
        super("StopAllAction");
    }

    public StopAllAction(Object targetLooper) {
        this();
    }

    @Override
    public Object nextSequential() {
        return ++current;
    }

    @Override
    public int hashCode() {
        return 31*super.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        } else if (!(obj instanceof StopAllAction)) {
            return false;
        } else {
            StopAllAction that = (StopAllAction) obj;
            return this.getName().equals(that.getName());
        }
    }

}

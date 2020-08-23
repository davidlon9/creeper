package com.dlong.creeper.control;

public class BreakAction extends AutoCurrentIndexAction {
    public BreakAction() {
        super("BREAK");
    }

    public BreakAction(Object targetLooper) {
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
        } else if (!(obj instanceof BreakAction)) {
            return false;
        }else{
            BreakAction that = (BreakAction) obj;
            return this.getName().equals(that.getName());
        }
    }

}

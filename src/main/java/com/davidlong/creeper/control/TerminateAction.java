package com.davidlong.creeper.control;

public class TerminateAction extends AbstractAction {
    private boolean isGlobal=false;

    TerminateAction() {
        super("TERMINATE");
    }

    public TerminateAction(boolean isGlobal) {
        this();
        this.isGlobal = isGlobal;
    }

    @Override
    public Integer nextSequential() {
        return -1;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }
}

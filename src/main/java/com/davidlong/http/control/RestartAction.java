package com.davidlong.http.control;

public class RestartAction extends AbstractAction {
    private boolean isGlobal=false;

    RestartAction() {
        super("RESTART");
    }

    public RestartAction(boolean isGlobal) {
        this();
        this.isGlobal = isGlobal;
    }

    @Override
    public Integer nextSequential() {
        return 1;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }
}

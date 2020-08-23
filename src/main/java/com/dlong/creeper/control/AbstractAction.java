package com.dlong.creeper.control;

public abstract class AbstractAction implements MoveAction {
    private String name;

    protected AbstractAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

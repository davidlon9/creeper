package com.davidlong.creeper.model.seq;

import com.davidlong.creeper.control.MoveAction;

public class MoveableEntity extends HandleableEntity {
    private MoveAction moveAction;

    public MoveAction getMoveAction() {
        return moveAction;
    }

    public void setMoveAction(MoveAction moveAction) {
        this.moveAction = moveAction;
    }
}

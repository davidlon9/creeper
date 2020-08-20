package com.davidlong.http.model.seq;

import com.davidlong.http.control.MoveAction;

public class MoveableEntity extends HandleableEntity {
    private MoveAction moveAction;

    public MoveAction getMoveAction() {
        return moveAction;
    }

    public void setMoveAction(MoveAction moveAction) {
        this.moveAction = moveAction;
    }
}

package com.dlong.creeper.control;

public class MoveActions {

    private MoveActions() {
    }

    public static MoveAction FORWARD(){
        return new ForwardAction();
    }

    public static MoveAction BACK(int interval){
        return new BackAction(interval);
    }

    public static MoveAction RETRY(int interval){
        return new RetryAction(interval);
    }

    public static MoveAction JUMP(Object jumpTo){
        return new JumpAction(jumpTo);
    }

    public static MoveAction JUMP(Object jumpTo,int interval){
        return new JumpAction(jumpTo,interval);
    }

    public static MoveAction CONTINUE(int interval){
        return new ContinueAction(interval);
    }

    public static MoveAction BREAK(){
        return new BreakAction();
    }

    public static MoveAction RESTART(boolean isGlobal){
        return new RestartAction(isGlobal);
    }

    public static MoveAction TERMINATE(boolean isGlobal){
        return new TerminateAction(isGlobal);
    }

    public static MoveAction TERMINATE(){
        return new TerminateAction(false);
    }
}

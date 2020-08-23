package com.dlong.creeper.control;
import com.dlong.creeper.exception.ExecutionException;

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

    public static MoveAction CONTINUE(int interval){
        return new ContinueAction(interval);
    }

    public static MoveAction JUMP(Object jumpTo){
        return new JumpAction(jumpTo);
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

    public static Object nextSequential(int currentIdx, MoveAction moveAction) throws ExecutionException, InterruptedException {
        if(moveAction ==null || moveAction instanceof ForwardAction){
            return ++currentIdx;
        }else if(moveAction instanceof BackAction){
            return --currentIdx;
        }else if(moveAction instanceof RestartAction){
            return 1;
        }else if(moveAction instanceof TerminateAction){
            return -1;
        }else if(moveAction instanceof RetryAction){
            return currentIdx;
        }else if(moveAction instanceof JumpAction){
            return ((JumpAction) moveAction).getJumpTo();
        }else{
            throw new ExecutionException("unkown move strategy "+ moveAction + " please try others");
        }
    }
}

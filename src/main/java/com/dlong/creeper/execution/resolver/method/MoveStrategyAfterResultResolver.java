package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.control.*;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

public class MoveStrategyAfterResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(MoveStrategyAfterResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        SequentialEntity seq = executionResult.getOrginalSeq();
        SequentialEntity next=null;
        if(methodResult instanceof ContinueAction){
            return new ContinueActionResolver().resolveResult(executionResult,context,methodResult);
        }else if(methodResult instanceof TerminateAction){
            executionResult.setFailed(true);
        }else if(methodResult instanceof RestartAction){
            executionResult.setFailed(true);
        }else if(methodResult instanceof MoveAction){
            MoveAction moveAction;
            if(methodResult instanceof AutoCurrentIndexAction){
                AutoCurrentIndexAction autoCurrentIndexStrategy = (AutoCurrentIndexAction) methodResult;
                autoCurrentIndexStrategy.setCurrent(executionResult.getOrginalIndex());
                moveAction = autoCurrentIndexStrategy;
            }else{
                moveAction = (MoveAction) methodResult;
            }
            executionResult.setActionResult(moveAction);

            Object nextIdentifier = moveAction.nextSequential();

            RequestChainEntity parent = seq.getRefParent();
            if(parent == null){
                parent = seq.getParent();
            }
            if(parent == null){
                logger.warn("can't resolve next for root entity "+seq);
                return executionResult;
            }

            if(moveAction instanceof RetryAction){
                logger.info(seq+" will be retry");
            }

            if(nextIdentifier instanceof Integer){
                Integer nextIndex = (Integer) nextIdentifier;
                next = context.getSequntialFinder().findSeqByFixedIndex(nextIndex, parent);
            }else if(nextIdentifier instanceof String){
                next = context.getSequntialFinder().findSeqByName((String) nextIdentifier, parent);
            }
        }
        executionResult.setNextSeq(next);
        return executionResult;
    }

//    public Object nextSequential(int currentIdx, MoveAction moveAction) throws ExecutionException {
//        if(moveAction ==null || moveAction instanceof ForwardAction){
//            return ++currentIdx;
//        }else if(moveAction instanceof BackAction){
//            return --currentIdx;
//        }else if(moveAction instanceof RestartAction){
//            return 1;
//        }else if(moveAction instanceof TerminateAction){
//            return -1;
//        }else if(moveAction instanceof RetryAction){
//            return currentIdx;
//        }else if(moveAction instanceof JumpAction){
//            return ((JumpAction) moveAction).getJumpTo();
//        }else{
//            throw new ExecutionException("unkown move strategy "+ moveAction + " please try others");
//        }
//    }
}

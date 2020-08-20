package com.davidlong.creeper.execution.looper;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.base.LoopableExecutor;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.LoopExecutionResult;
import com.davidlong.creeper.model.Multiple;
import com.davidlong.creeper.model.seq.LoopableEntity;
import com.davidlong.creeper.model.seq.control.WhileLooper;
import com.davidlong.creeper.model.seq.control.Looper;
import org.apache.log4j.Logger;

import java.io.IOException;

public class WhileExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger=Logger.getLogger(WhileExecuteLooper.class);

    public WhileExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,WhileLooper.class);
    }

    @Override
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException, IOException {
        Looper looper = loopableEntity.getLooper();
        WhileLooper whileLooper = (WhileLooper) looper;
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;

        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);
        Boolean condition;
        int count = 1;
        do {
            if(isMultipleShutdown(multiple)){
                loopResult.setOtherThreadSuccessed(true);
                logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                break;
            }
//            if (Thread.currentThread().isInterrupted()) {
//                loopResult.setOtherThreadSuccessed(true);
//                logger.warn("Looper is break probably cause by other thread successed!");
//                break;
//            }
            condition = getContext().getExpressionParser().parse(whileLooper.getConiditionExpression(), Boolean.class);
            if(!condition){
                break;
            }
            logger.info("* Loop "+count+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());

            ExecutionResult innerResult = executor.doExecute(loopableEntity);
            loopResult.addLoopResult(innerResult);

            if(isBreak(innerResult)){
                loopResult.setNextSeq(innerResult.getNextSeq());
                break;
            }
            count++;
        }while (condition);
        loopResult.setLoopOver(true);
        return loopResult;
    }

    public boolean loopCondition(){
        return true;
    }
}

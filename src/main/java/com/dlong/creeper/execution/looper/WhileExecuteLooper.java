package com.dlong.creeper.execution.looper;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.base.LoopableExecutor;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.control.WhileLooper;
import com.dlong.creeper.model.seq.multi.Multiple;
import org.apache.log4j.Logger;

import java.io.IOException;

public class WhileExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger= Logger.getLogger(WhileExecuteLooper.class);

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
        loopResult.setLoopNum(count);
        loopResult.setTotalNum(count);
        return loopResult;
    }

    public boolean loopCondition(){
        return true;
    }
}

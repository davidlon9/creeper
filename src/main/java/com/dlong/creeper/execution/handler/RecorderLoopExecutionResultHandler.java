package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import org.apache.log4j.Logger;

/**
 * 此handler afterExecute()必需在 handlerMethod调用后执行
 */
public class RecorderLoopExecutionResultHandler implements LoopExecutionResultHandler {
    private static Logger logger = Logger.getLogger(RecorderLoopExecutionResultHandler.class);

    @Override
    public void beforeExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ChainContext context) throws ExecutionException {
    }

    @Override
    public void afterExecute(LoopExecutionResult<? extends LoopableEntity> executionResult, ChainContext context) throws ExecutionException {
//        LoopableEntity loopableEntity = executionResult.getOrginalSeq();
//        Looper looper = loopableEntity.getLooper();
//        if(looper!=null){
//            int loopNum = executionResult.getLoopNum();
//            int totalLoopNum = executionResult.getTotalNum();
//            int i = totalLoopNum - loopNum;
//            if(loopNum){
//
//            }
//        }
    }
}

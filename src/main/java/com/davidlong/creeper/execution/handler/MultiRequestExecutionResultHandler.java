package com.davidlong.creeper.execution.handler;

import com.davidlong.creeper.control.StopAllAction;
import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.Multiple;
import com.davidlong.creeper.model.seq.RequestEntity;
import com.davidlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

/**
 * 处理Request线程执行完成后
 * 必需在HandlerMethodExecutionHandler之后
 */
public class MultiRequestExecutionResultHandler implements RequestExecutionResultHandler {
    private static Logger logger = Logger.getLogger(MultiRequestExecutionResultHandler.class);

    @Override
    public void beforeExecute(ExecutionResult<? extends RequestEntity> executionResult, ExecutionContext context) throws ExecutionException {

    }

    @Override
    public void afterExecute(ExecutionResult<? extends RequestEntity> executionResult, ExecutionContext context) throws ExecutionException {
        handleMove(executionResult);
    }

    /**
     * 其中一个线程执行成功并移动了，判断是否结束其他线程 afterResult
     * 返回StopAllStrategy 或者 isMoveStopAll为true
     * @param executionResult
     */
    public void handleMove(ExecutionResult executionResult) {
        SequentialEntity nextSeq = executionResult.getNextSeq();
        if(nextSeq==null){
            return;
        }
        SequentialEntity orginalSeq = executionResult.getOrginalSeq();
        if (orginalSeq instanceof Multiple) {
            Object afterResult = executionResult.getAfterResult();
            if(afterResult!=null){
                Multiple multiple=(Multiple)orginalSeq;
                if (afterResult.getClass().equals(StopAllAction.class) ||
                        (multiple.isMoveStopAll() && nextSeq!=orginalSeq)) {
                    multiple.shutdown();
                }
            }
        }
    }

//    /**
//     * 其中一个线程执行成功并移动了，判断是否结束其他线程 afterResult
//     * 返回StopAllStrategy 或者 isMoveStopAll为true
//     * @param executionResult
//     */
//    public void handleMove(ExecutionResult executionResult) {
//        SequentialEntity nextSeq = executionResult.getNextSeq();
//        if(nextSeq==null){
//            return;
//        }
//        SequentialEntity orginalSeq = executionResult.getOrginalSeq();
//        if (orginalSeq instanceof Multiple) {
//            Object afterResult = executionResult.getAfterResult();
//            if(afterResult!=null){
//                Multiple multiple=(Multiple)orginalSeq;
//                if (afterResult.getClass().equals(StopAllAction.class) ||
//                        (multiple.isMoveStopAll() && nextSeq!=orginalSeq)) {
//                    Thread[] threads = multiple.getThreads();
//                    for (Thread thread : threads) {
//                        if(!thread.equals(Thread.currentThread())){
//                            thread.interrupt();
//                        }
//                    }
//                }
//            }
//        }
//    }
}

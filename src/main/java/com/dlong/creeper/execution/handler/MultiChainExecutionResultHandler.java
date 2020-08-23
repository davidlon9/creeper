package com.dlong.creeper.execution.handler;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.handler.help.ThreadLocalMap;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.multi.Multiple;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * 处理Chain多线程执行完成后
 * 必需在ChainHandlerMethodExecutionHandler之后
 */
public class MultiChainExecutionResultHandler implements ChainExecutionResultHandler {
    private static Logger logger=Logger.getLogger(MultiChainExecutionResultHandler.class);

    @Override
    public void beforeExecute(ExecutionResult<? extends RequestChainEntity> executionResult,ExecutionContext context) throws ExecutionException {
    }

    @Override
    public void afterExecute(ExecutionResult<? extends RequestChainEntity> executionResult,ExecutionContext context) throws ExecutionException {
        handleMove(executionResult);
    }

    /**
     * 其中一个线程执行成功并移动了，判断是否结束其他线程 afterResult
     * 返回StopAllStrategy 或者 isMoveStopAll为true
     * @param executionResult
     */
    public void handleMove(ExecutionResult executionResult) {
        SequentialEntity nextSeq = executionResult.getNextSeq();
        if(nextSeq==null && executionResult.isFailed()){
            return;
        }
        SequentialEntity orginalSeq = executionResult.getOrginalSeq();
        if (orginalSeq instanceof Multiple) {
            Multiple multiple=(Multiple)orginalSeq;
            if (multiple.isMoveStopAll() && nextSeq!=orginalSeq) {
                //shutdown该Chain下的Seqs的所有线程，当前线程已经执行结束，所以打断也没有影响
                shutdownAll((RequestChainEntity) orginalSeq);
            }
        }
    }

    private void shutdownAll(RequestChainEntity chainEntity) {
        List<SequentialEntity> sequentialList = chainEntity.getSequentialList();
        for (SequentialEntity sequentialEntity : sequentialList) {
            if(sequentialEntity instanceof Multiple){
                Multiple m = (Multiple) sequentialEntity;
                ThreadLocalMap<ExecutorService> localThreadPool = m.getLocalThreadPool();
                Map<Thread, ExecutorService> threadLocalMap = localThreadPool.getThreadLocalMap();
                Set<Map.Entry<Thread, ExecutorService>> entries = threadLocalMap.entrySet();
                for (Map.Entry<Thread, ExecutorService> entry : entries) {
                    if(entry.getValue()!=null){
                        entry.getValue().shutdown();
                    }
                }
            }
        }
    }

}

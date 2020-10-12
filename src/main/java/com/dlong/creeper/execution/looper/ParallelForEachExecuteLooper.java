package com.dlong.creeper.execution.looper;

import com.dlong.creeper.control.RetryAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.base.LoopableExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.control.ParallelForEachLooper;
import com.dlong.creeper.model.seq.multi.Multiple;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ParallelForEachExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger= Logger.getLogger(ParallelForEachExecuteLooper.class);

    public ParallelForEachExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ParallelForEachLooper.class);
    }

    @Override
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException,IOException {
        ChainContext chainContext = getContext();
        ContextParamStore contextStore = chainContext.getContextStore();

        Looper looper = loopableEntity.getLooper();
        ParallelForEachLooper forEachLooper = (ParallelForEachLooper) looper;
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;

        String itemsContextKey = forEachLooper.getItemsContextKey();
        Object value = contextStore.getValue(itemsContextKey);

        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);
        if (value instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) value;
            AtomicInteger count=new AtomicInteger(0);
            AtomicBoolean isStop=new AtomicBoolean(false);
            AtomicReference<Exception> exception=new AtomicReference<>(null);
            collection.parallelStream().forEach(obj->{
                if(isStop.get()){
                    return;
                }
                if(isMultipleShutdown(multiple)){
                    loopResult.setOtherThreadSuccessed(true);
                    logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                    isStop.set(true);
                    return;
                }

                contextStore.addParam(forEachLooper.getItemName(),obj);
                logger.info("* Parallel Loop "+count.get()+"of "+collection.size()+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());
                ExecutionResult<T> innerResult;
                try {
                    innerResult = executor.doExecute(loopableEntity);
                    if(isBreak(innerResult)){
                        isStop.set(true);
                        return;
                    }
                    if(innerResult.getActionResult() instanceof RetryAction){
                        logger.info("* Parallel Loop "+count.get()+" of "+collection.size()+" of "+loopableEntity+" will be retry");
                        innerResult = retryInLoop(loopableEntity, loopResult);
                        if(isBreak(innerResult)){
                            isStop.set(true);
                            return;
                        }
                    }else{
                        count.addAndGet(1);
                    }
                } catch (IOException | ExecutionException e) {
                    exception.set(e);
                    isStop.set(true);
                    return;
                }
                loopResult.setNextSeq(innerResult.getNextSeq());
                loopResult.addLoopResult(innerResult);
            });
            Exception e = exception.get();
            if(e !=null){
                throw new ExecutionException(e);
            }
            loopResult.setLoopNum(count.get());
            loopResult.setTotalNum(collection.size());
        }else if(value == null){
            throw new ExecutionException("itemsContextKey "+itemsContextKey+" mapped paramValue is null");
        }else{
            throw new ExecutionException("itemsContextKey "+itemsContextKey+" mapped paramValue is not a Collection");
        }
        new AutoNextSeqResultResolver().afterExecuteResovle(loopResult,getContext());
        return loopResult;
    }

    private ExecutionResult<T> retryInLoop(T loopableEntity, LoopExecutionResult<T> loopResult) throws IOException, ExecutionException {
        ExecutionResult<T> retryResult = executor.doExecute(loopableEntity);
        if(retryResult.getActionResult() instanceof RetryAction){
            logger.info("* Loop of "+loopableEntity+" will be retry again");
            return retryInLoop(loopableEntity, loopResult);
        }
        return retryResult;
    }

}

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
import com.dlong.creeper.model.seq.control.ForEachLooper;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.multi.Multiple;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

public class ForEachExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger= Logger.getLogger(ForEachExecuteLooper.class);

    public ForEachExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ForEachLooper.class);
    }

    @Override
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException,IOException {
        ChainContext chainContext = getContext();
        ContextParamStore contextStore = chainContext.getContextStore();

        Looper looper = loopableEntity.getLooper();
        ForEachLooper forEachLooper = (ForEachLooper) looper;
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;

        String itemsContextKey = forEachLooper.getItemsContextKey();
        Object value = contextStore.getValue(itemsContextKey);

        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);
        if (value instanceof Collection) {
            int count = 1;
            Collection collection = (Collection) value;
            for (Object obj : collection) {
                if(isMultipleShutdown(multiple)){
                    loopResult.setOtherThreadSuccessed(true);
                    logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                    break;
                }

                contextStore.addParam(forEachLooper.getItemName(),obj);

                logger.info("* Loop "+count+" of "+collection.size()+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());
                ExecutionResult<T> innerResult = executor.doExecute(loopableEntity);
                if(isBreak(innerResult)){
                    break;
                }
                if(innerResult.getActionResult() instanceof RetryAction){
                    logger.info("* Loop "+count+" of "+collection.size()+" of "+loopableEntity+" will be retry");
                    innerResult = retryInLoop(loopableEntity, loopResult);
                    if(isBreak(innerResult)){
                        break;
                    }
                }else{
                    count++;
                }
                loopResult.setNextSeq(innerResult.getNextSeq());
                loopResult.addLoopResult(innerResult);
            }
            loopResult.setLoopNum(count);
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

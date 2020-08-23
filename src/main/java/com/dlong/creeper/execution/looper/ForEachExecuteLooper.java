package com.dlong.creeper.execution.looper;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.base.LoopableExecutor;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.execution.context.ParamStore;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.multi.Multiple;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.control.ForEachLooper;
import com.dlong.creeper.model.seq.control.Looper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

public class ForEachExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T>{
    private static Logger logger=Logger.getLogger(ForEachExecuteLooper.class);

    public ForEachExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ForEachLooper.class);
    }

    @Override
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException,IOException {
        ExecutionContext executionContext = getContext();
        ParamStore<String, Object> contextStore = executionContext.getContextStore();

        Looper looper = loopableEntity.getLooper();
        ForEachLooper forEachLooper = (ForEachLooper) looper;
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;

        String itemsContextKey = forEachLooper.getItemsContextKey();
        Object value = contextStore.getValue(itemsContextKey);

        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);
        if (value instanceof Collection) {
            int count = 1;
            Collection collection = (Collection) value;
            for (Object next : collection) {
                if(isMultipleShutdown(multiple)){
                    loopResult.setOtherThreadSuccessed(true);
                    logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                    break;
                }

                contextStore.addParam(forEachLooper.getItemName(),next);

                logger.info("* Loop "+count+" of "+collection.size()+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());
                ExecutionResult<T> innerResult = executor.doExecute(loopableEntity);

                loopResult.addLoopResult(innerResult);

                if(isBreak(innerResult)){
                    loopResult.setNextSeq(innerResult.getNextSeq());
                    break;
                }
                count++;
            }
            loopResult.setLoopOver(true);
        }else if(value == null){
            throw new ExecutionException("itemsContextKey "+itemsContextKey+" mapped paramValue is null");
        }else{
            throw new ExecutionException("itemsContextKey "+itemsContextKey+" mapped paramValue is not a Collection");
        }
        new AutoNextSeqResultResolver().afterExecuteResovle(loopResult,getContext());
        return loopResult;
    }
}

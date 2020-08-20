package com.davidlong.http.execution.looper;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.base.LoopableExecutor;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.resolver.AutoNextSeqResultResolver;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.execution.context.ParamStore;
import com.davidlong.http.model.LoopExecutionResult;
import com.davidlong.http.model.Multiple;
import com.davidlong.http.model.seq.LoopableEntity;
import com.davidlong.http.model.seq.control.ForEachLooper;
import com.davidlong.http.model.seq.control.Looper;
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

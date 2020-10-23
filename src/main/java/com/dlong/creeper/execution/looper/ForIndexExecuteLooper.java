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
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.control.ForIndexLooper;
import com.dlong.creeper.model.seq.multi.Multiple;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

public class ForIndexExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger= Logger.getLogger(ForIndexExecuteLooper.class);

    public ForIndexExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ForIndexLooper.class);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException, IOException {
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;
        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);

        ContextParamStore contextStore = getContext().getContextStore();
        ForIndexLooper forIndexLooper = (ForIndexLooper) loopableEntity.getLooper();

        Integer start = getStartIndex(contextStore,forIndexLooper);
        Integer end = getEndIndex(contextStore,forIndexLooper);
        Collection<Object> prediectItems = forIndexLooper.getPredictItems();
        int i;
        for (i = start; i <= end; i++) {
            if(isMultipleShutdown(multiple)){
                loopResult.setOtherThreadSuccessed(true);
                logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                break;
            }
            if(forIndexLooper.isPredicted() && !prediectItems.contains(i)){
                continue;
            }
            logger.info("* Loop "+i+" of "+end+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());

            contextStore.addParam(forIndexLooper.getIndexName(),i);

            ExecutionResult<T> innerResult = executor.doExecute(loopableEntity);

            if(isBreak(innerResult)){
                loopResult.setNextSeq(innerResult.getNextSeq());
                break;
            }

            if(innerResult.getActionResult() instanceof RetryAction){
                logger.info("* Loop "+i+" of "+end+" of "+loopableEntity+" will be retry");
                i--;
                continue;
            }
            loopResult.addLoopResult(innerResult);
        }
        loopResult.setLoopNum(i);
        loopResult.setTotalNum(end);
        new AutoNextSeqResultResolver().afterExecuteResovle(loopResult,getContext());
        return loopResult;
    }

    @Override
    public void doPredict(LoopExecutionResult<T> result, ChainContext context) throws ExecutionException {
        T loopableEntity = result.getOrginalSeq();
        ContextParamStore contextStore = getContext().getContextStore();
        ForIndexLooper forIndexLooper = (ForIndexLooper) loopableEntity.getLooper();
        Integer start = getStartIndex(contextStore, forIndexLooper);
        Integer end = getEndIndex(contextStore, forIndexLooper);
        if(loopableEntity instanceof RequestEntity){
            RequestEntity requestEntity = (RequestEntity) loopableEntity;
            int i;
            for (i = start; i <= end; i++) {
                contextStore.addParam(forIndexLooper.getIndexName(),i);
                forIndexLooper.putPredictUrlItem(requestEntity.buildUrl(context),i);
            }
            forIndexLooper.setPredicted(true);
        }
    }

    private Integer getEndIndex(ContextParamStore contextStore, ForIndexLooper forIndexLooper) {
        Integer value = (Integer) contextStore.getValue("end");
        if(value == null){
            value = contextStore.getExpressionParser().parse(forIndexLooper.getEnd(), Integer.class);
            contextStore.addParam("end",value);
        }
        return value;
    }

    private Integer getStartIndex(ContextParamStore contextStore, ForIndexLooper forIndexLooper) {
        Integer value = (Integer) contextStore.getValue("start");
        if(value == null){
            value = contextStore.getExpressionParser().parse(forIndexLooper.getStart(), Integer.class);
            contextStore.addParam("start",value);
        }
        return value;
    }
}

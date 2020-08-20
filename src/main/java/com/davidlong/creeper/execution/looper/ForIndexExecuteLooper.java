package com.davidlong.creeper.execution.looper;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.base.LoopableExecutor;
import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.LoopExecutionResult;
import com.davidlong.creeper.model.Multiple;
import com.davidlong.creeper.model.seq.LoopableEntity;
import com.davidlong.creeper.model.seq.control.ForIndexLooper;
import com.davidlong.creeper.model.seq.control.Looper;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ForIndexExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger=Logger.getLogger(ForIndexExecuteLooper.class);

    public ForIndexExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ForIndexLooper.class);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException, IOException {
        ExecutionContext context = getContext();
        ContextParamStore contextStore = getContext().getContextStore();

        Looper looper = loopableEntity.getLooper();
        ForIndexLooper forIndexEntity= (ForIndexLooper) looper;
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;

        String startExpr = forIndexEntity.getStart();
        String endExpr = forIndexEntity.getEnd();

        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);

        Integer start = context.getExpressionParser().parse(startExpr,Integer.class);
        Integer end = context.getExpressionParser().parse(endExpr,Integer.class);
        contextStore.addParam("start",start);
        contextStore.addParam("end",end);

        for (int i = start; i <= end; i++) {
            if(isMultipleShutdown(multiple)){
                loopResult.setOtherThreadSuccessed(true);
                logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                break;
            }
            logger.info("* Loop "+i+" of "+end+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());

            contextStore.addParam(forIndexEntity.getIndexName(),i);

            ExecutionResult<T> innerResult = executor.doExecute(loopableEntity);

            loopResult.addLoopResult(innerResult);

            if(isBreak(innerResult)){
                loopResult.setNextSeq(innerResult.getNextSeq());
                break;
            }
        }
        loopResult.setLoopOver(true);
        new AutoNextSeqResultResolver().afterExecuteResovle(loopResult,getContext());
        return loopResult;
    }

}

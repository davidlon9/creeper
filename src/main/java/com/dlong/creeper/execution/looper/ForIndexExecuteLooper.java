package com.dlong.creeper.execution.looper;

import com.dlong.creeper.control.RetryAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.base.LoopableExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.multi.Multiple;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.control.ForIndexLooper;
import com.dlong.creeper.model.seq.control.Looper;
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
        ChainContext context = getContext();
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

            if(innerResult.getActionResult() instanceof RetryAction){
                logger.info("* Loop "+i+" of "+end+" of "+loopableEntity+" will be retry");
                i--;
            }
        }
        loopResult.setLoopOver(true);
        new AutoNextSeqResultResolver().afterExecuteResovle(loopResult,getContext());
        return loopResult;
    }
}

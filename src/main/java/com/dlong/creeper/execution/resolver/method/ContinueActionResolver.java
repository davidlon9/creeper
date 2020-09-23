package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.control.ContinueAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.exception.UnsupportedReturnTypeException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import com.dlong.creeper.model.seq.control.Looper;
import org.apache.log4j.Logger;

public class ContinueActionResolver implements HandlerMethodResultResolver {
    private static Logger logger= Logger.getLogger(ContinueActionResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        if(methodResult instanceof ContinueAction){
            SequentialEntity orginalSeq = executionResult.getOrginalSeq();
            executionResult.setSkipExecute(true);
            if(orginalSeq instanceof LoopableEntity){
                LoopableEntity loopableEntity = (LoopableEntity) orginalSeq;
                Looper looper = loopableEntity.getLooper();
                if(looper != null){
                    //自身循环Continue继续执行当前对象
                    executionResult.setNextSeq(orginalSeq);
                    logger.info("before handler return ContinueAction to skip current loop in self loop execution");
                }else{
                    RequestChainEntity parent = loopableEntity.getParent();
                    if(parent!=null && parent.getLooper()!=null){
                        //父链循环Continue跳出循环
                        executionResult.setNextSeq(null);
                        logger.info("before handler return ContinueAction to skip current loop in parent chain loop execution");
                    }
                }
            }
        }
        return executionResult;
    }
}

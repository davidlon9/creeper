package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.control.BreakAction;
import com.dlong.creeper.control.ContinueAction;
import com.dlong.creeper.control.RetryAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ExecutionResult;
import org.apache.log4j.Logger;

public class SimpleLoopAfterResultResolver implements HandlerMethodResultResolver {
    protected Logger logger=Logger.getLogger(SimpleLoopAfterResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        if(methodResult == null){
            logger.info("Looper处理方法，返回null未达预期结果，继续执行");
            new MoveStrategyAfterResultResolver().resolveResult(executionResult,context, new ContinueAction(1000));
            return executionResult;
        }
        if(methodResult instanceof Boolean){
            if(!(Boolean)methodResult){
                //Loop执行时，返回false表示未达预期结果，则继续执行
                new MoveStrategyAfterResultResolver().resolveResult(executionResult,context, new ContinueAction(1000));
                logger.info("Looper处理方法，返回false未达预期结果，继续执行");
            }else{
                //Loop执行时，返回true表示执行成功，因此跳出循环，并执行下一请求
                new MoveStrategyAfterResultResolver().resolveResult(executionResult,context, new BreakAction());
                logger.info("Looper处理方法，返回true执行成功，跳出循环，并执行下一请求");
            }
        }
        return executionResult;
    }
}

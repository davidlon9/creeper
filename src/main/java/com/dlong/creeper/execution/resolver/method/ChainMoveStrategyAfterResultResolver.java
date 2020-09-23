package com.dlong.creeper.execution.resolver.method;

import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.RestartAction;
import com.dlong.creeper.control.TerminateAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ChainExecutionResult;
import com.dlong.creeper.model.result.ExecutionResult;
import org.apache.log4j.Logger;

public class ChainMoveStrategyAfterResultResolver extends MoveStrategyAfterResultResolver {
    private static Logger logger=Logger.getLogger(ChainMoveStrategyAfterResultResolver.class);

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ChainContext context, Object methodResult) throws ExecutionException {
        super.resolveResult(executionResult,context,methodResult);
        if (executionResult instanceof ChainExecutionResult) {
            ChainExecutionResult chainResult= (ChainExecutionResult) executionResult;
            ExecutionResult finalResult = chainResult.getFinalResult();
            MoveAction actionResult = finalResult.getActionResult();
            if(actionResult instanceof RestartAction){
                chainResult.setActionResult(actionResult);
            }else if(actionResult instanceof TerminateAction){
                chainResult.setActionResult(actionResult);
                chainResult.setFailed(true);
            }
        }
        return executionResult;
    }
}

package com.dlong.creeper.model.result;

import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.seq.RequestChainEntity;

import java.util.ArrayList;
import java.util.List;

public class ChainExecutionResult<T extends RequestChainEntity> extends ExecutionResult<T> {
    private List<ExecutionResult> chainResults = new ArrayList<>();
    private ExecutionResult finalResult;
    private ChainContext context;

    public ChainExecutionResult(T orginalChain) {
        super(orginalChain);
    }

    public void addChainResult(ExecutionResult executionResult){
        this.chainResults.add(executionResult);
    }

    public List<ExecutionResult> getChainResult() {
        return chainResults;
    }

    public ExecutionResult getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(ExecutionResult finalResult) {
        this.finalResult = finalResult;
    }

    public ChainContext getContext() {
        return context;
    }

    public void setContext(ChainContext context) {
        this.context = context;
    }
}

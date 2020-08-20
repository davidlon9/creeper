package com.davidlong.creeper.model.result;

import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.seq.RequestChainEntity;

import java.util.ArrayList;
import java.util.List;

public class ChainExecutionResult<T extends RequestChainEntity> extends ExecutionResult<T> {
    private List<ExecutionResult> chainResults = new ArrayList<>();
    private ExecutionResult finalResult;
    private ExecutionContext context;

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

    public ExecutionContext getContext() {
        return context;
    }

    public void setContext(ExecutionContext context) {
        this.context = context;
    }
}

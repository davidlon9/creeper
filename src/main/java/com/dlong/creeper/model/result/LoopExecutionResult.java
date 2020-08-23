package com.dlong.creeper.model.result;

import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.SequentialEntity;

import java.util.ArrayList;
import java.util.List;

public class LoopExecutionResult<T extends LoopableEntity> extends ExecutionResult<T> {
    private boolean isLoopOver = false;
    private boolean isOtherThreadSuccessed = false;

    private List<ExecutionResult> loopResults = new ArrayList<>();

    public LoopExecutionResult(T orginalSeq) {
        super(orginalSeq);
    }

    public LoopExecutionResult(T orginalSeq, SequentialEntity nextSeq) {
        super(orginalSeq, nextSeq);
    }

    public boolean isLoopOver() {
        return isLoopOver;
    }

    public void setLoopOver(boolean loopOver) {
        isLoopOver = loopOver;
    }

    public void addLoopResult(ExecutionResult executionResult){
        this.loopResults.add(executionResult);
    }

    public List<ExecutionResult> getLoopResults() {
        return loopResults;
    }

    public boolean isFailed(){
        if(isOtherThreadSuccessed){
            return false;
        }
        for (ExecutionResult loopResult : loopResults) {
            if(!loopResult.isFailed()){
                return false;
            }
        }
        return true;
    }

    public boolean isOtherThreadSuccessed() {
        return isOtherThreadSuccessed;
    }

    public void setOtherThreadSuccessed(boolean otherThreadSuccessed) {
        isOtherThreadSuccessed = otherThreadSuccessed;
    }
}

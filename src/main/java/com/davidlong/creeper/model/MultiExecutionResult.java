package com.davidlong.http.model;

import com.davidlong.http.model.seq.LoopableEntity;
import com.davidlong.http.model.seq.SequentialEntity;
import com.davidlong.http.model.seq.multi.MultiRequestChainEntity;

import java.util.ArrayList;
import java.util.List;

public class MultiExecutionResult<T extends LoopableEntity> extends ExecutionResult<T> {
    private boolean isRunOver = false;

    private List<ExecutionResult> threadResults = new ArrayList<>();

    public MultiExecutionResult(T orginalSeq) {
        super(orginalSeq);
    }

    public MultiExecutionResult(T orginalSeq, SequentialEntity nextSeq) {
        super(orginalSeq, nextSeq);
    }

    public void addThreadResult(ExecutionResult executionResult){
        this.threadResults.add(executionResult);
    }

    public List<ExecutionResult> getThreadResults() {
        return threadResults;
    }

    public boolean isRunOver() {
        return isRunOver;
    }

    public void setRunOver(boolean runOver) {
        isRunOver = runOver;
    }

    public boolean isFailed(){
        for (ExecutionResult threadResult : threadResults) {
            if(threadResult!=null && threadResult.isFailed()){
                return true;
            }
        }
        return false;
    }

    public boolean isOtherThreadSuccessed() {
        for (ExecutionResult threadResult : threadResults) {
            if (threadResult instanceof LoopExecutionResult) {
                if(threadResult!=null && !((LoopExecutionResult)threadResult).isOtherThreadSuccessed()){
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }

}

package com.dlong.creeper.model.result;

import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.SequentialEntity;

import java.util.ArrayList;
import java.util.List;

public class LoopExecutionResult<T extends LoopableEntity> extends ExecutionResult<T> {
    private boolean isLoopOver = false;
    private boolean isOtherThreadSuccessed = false;
    private int loopNum = 0;
    private int totalNum = 0;
    private List<ExecutionResult> loopResults = new ArrayList<>();

    public LoopExecutionResult(T orginalSeq) {
        super(orginalSeq);
    }

    public LoopExecutionResult(T orginalSeq, SequentialEntity nextSeq) {
        super(orginalSeq, nextSeq);
    }

    public boolean isLoopOver() {
        return loopNum == totalNum;
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
        if(this.loopResults.size()==0){
            return false;
        }
        for (ExecutionResult loopResult : loopResults) {
            if(!loopResult.isFailed()){
                return false;
            }
        }
        return true;
    }

    public int getSuccessedLoopNum(){
        int num = loopResults.size();
        for (ExecutionResult loopResult : loopResults) {
            if(!loopResult.isFailed()){
                num--;
            }
        }
        return num;
    }

    public int getLoopNum() {
        return loopNum;
    }

    public void setLoopNum(int loopNum) {
        this.loopNum = loopNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public void setLoopResults(List<ExecutionResult> loopResults) {
        this.loopResults = loopResults;
    }

    public boolean isOtherThreadSuccessed() {
        return isOtherThreadSuccessed;
    }

    public void setOtherThreadSuccessed(boolean otherThreadSuccessed) {
        isOtherThreadSuccessed = otherThreadSuccessed;
    }
}

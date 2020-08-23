package com.dlong.creeper.model.result;

import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.apache.http.HttpResponse;

public class ExecutionResult<T extends SequentialEntity> {
    private T orginalSeq;
    private SequentialEntity nextSeq;

    private Object afterResult;
    private Object beforeResult;
    private MoveAction actionResult;
    private HttpResponse httpResponse;

    private boolean isExecuted = false;
    private boolean isSkipExecute = false;
    private boolean isFailed = false;

    public ExecutionResult(T orginalSeq) {
        this.orginalSeq = orginalSeq;
    }

    public ExecutionResult(T orginalSeq, SequentialEntity nextSeq) {
        this.orginalSeq = orginalSeq;
        this.nextSeq = nextSeq;
    }

    public void setAfterResult(Object afterResult) {
        this.afterResult = afterResult;
        if(afterResult instanceof MoveAction){
            this.actionResult = (MoveAction) afterResult;
        }
    }

    public T getOrginalSeq() {
        return orginalSeq;
    }

    public int getOrginalIndex() {
        return orginalSeq.getIndex();
    }

    public void setOrginalSeq(T orginalSeq) {
        this.orginalSeq = orginalSeq;
    }

    public SequentialEntity getNextSeq() {
        return nextSeq;
    }

    public void setNextSeq(SequentialEntity nextSeq) {
        this.nextSeq = nextSeq;
    }

    public Object getAfterResult() {
        return afterResult;
    }

    public Object getBeforeResult() {
        return beforeResult;
    }

    public void setBeforeResult(Object beforeResult) {
        this.beforeResult = beforeResult;
    }

    public MoveAction getActionResult() {
        return actionResult;
    }

    public void setActionResult(MoveAction actionResult) {
        this.actionResult = actionResult;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

    public boolean isSkipExecute() {
        return isSkipExecute;
    }

    public void setSkipExecute(boolean skipExecute) {
        isSkipExecute = skipExecute;
    }

    public RequestChainEntity getOrginalParent(){
        return this.orginalSeq.getParent();
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }
}

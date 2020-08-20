package com.davidlong.http.model.seq;

import com.davidlong.http.execution.handler.entity.ChainAfterHandler;
import com.davidlong.http.execution.handler.entity.ChainBeforeHandler;
import com.davidlong.http.execution.handler.entity.ChainExecutionHandler;
import com.davidlong.http.model.log.RequestLogInfo;
import com.davidlong.http.model.log.ResponseLogInfo;

import java.util.*;

public class RequestChainEntity extends LoopableEntity{
    private Class chainClass;
    private Object chainInstance;
    private List<SequentialEntity> sequentialList;
    private RequestLogInfo requestLogInfo;
    private ResponseLogInfo responseLogInfo;
    private Map<String,RequestEntity> requestNameMap = new LinkedHashMap<>();

    public List<SequentialEntity> getSequentialList() {
        return sequentialList!=null?sequentialList:new ArrayList<>(3);
    }

    public void setSequentialList(List<SequentialEntity> sequentialList) {
        this.sequentialList = sequentialList;
    }

    public Class getChainClass() {
        return chainClass;
    }

    public void setChainClass(Class chainClass) {
        this.chainClass = chainClass;
    }

    public Object getChainInstance() {
        return chainInstance;
    }

    public void setChainInstance(Object chainInstance) {
        this.chainInstance = chainInstance;
    }

    public SequentialEntity getFirstSequential(){
        return this.sequentialList.get(0);
    }

    public RequestLogInfo getRequestLogInfo() {
        return requestLogInfo;
    }

    public void setRequestLogInfo(RequestLogInfo requestLogInfo) {
        this.requestLogInfo = requestLogInfo;
    }

    public ResponseLogInfo getResponseLogInfo() {
        return responseLogInfo;
    }

    public void setResponseLogInfo(ResponseLogInfo responseLogInfo) {
        this.responseLogInfo = responseLogInfo;
    }

    public void setExecutionHandler(ChainExecutionHandler handler) {
        super.setExecutionHandler(handler);
    }

    public void setAfterHandler(ChainAfterHandler handler) {
        super.setAfterHandler(handler);
    }

    public void setBeforeHandler(ChainBeforeHandler handler) {
        super.setBeforeHandler(handler);
    }

    public Map<String, RequestEntity> getRequestNameMap() {
        return requestNameMap;
    }

    public List<RequestEntity> getRequestEntityList() {
        List<RequestEntity> entities=new ArrayList<>();
        for (SequentialEntity sequentialEntity : sequentialList) {
            if (sequentialEntity instanceof RequestEntity) {
                entities.add((RequestEntity) sequentialEntity);
            }
        }
        return entities;
    }
}

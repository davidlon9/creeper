package com.dlong.creeper.model.seq;

import com.dlong.creeper.execution.handler.entity.ChainAfterHandler;
import com.dlong.creeper.execution.handler.entity.ChainBeforeHandler;
import com.dlong.creeper.execution.handler.entity.ChainExecutionHandler;
import com.dlong.creeper.model.log.RequestLogInfo;
import com.dlong.creeper.model.log.ResponseLogInfo;

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
        if(chainInstance==null){
            return;
        }
        Class<?> chainClz = chainInstance.getClass();
        if (chainClz.equals(this.chainClass)) {
            this.chainInstance = chainInstance;
        }else{
            for (SequentialEntity sequentialEntity : this.sequentialList) {
                if(sequentialEntity instanceof RequestChainEntity){
                    ((RequestChainEntity) sequentialEntity).setChainInstance(chainInstance);
                }
            }
        }
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

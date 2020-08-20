package com.davidlong.http.model;

import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.model.seq.SequentialEntity;

import java.util.*;

public class ChainIndexInfo{
    private RequestChainEntity chainEntity;

    private Map<Integer,Integer> fixedSeqListIndexMap;//当前chain的修复索引的映射
    private Map<String,SequentialEntity> fullNameSeqMap;//所有sequential的全名映射

    private Integer endSeqIndex=1;

    public ChainIndexInfo(RequestChainEntity chainEntity) {
        this.chainEntity = chainEntity;
        fixedSeqListIndexMap = this.getFixedSeqListIndexMap();
        fullNameSeqMap = this.getFullNameSeqMap();
        init();
    }

    public void init() {
        initFullNameSeqMap();
        refreshExecutingIndexInfo();
    }

    public void refreshExecutingIndexInfo() {
        Map<Integer, Integer> fixedSeqListIndexMap = this.getFixedSeqListIndexMap();
        if(fixedSeqListIndexMap.size()>0){
            fixedSeqListIndexMap.clear();
        }
        List<SequentialEntity> sequentialList = this.chainEntity.getSequentialList();
        int size = sequentialList.size();
        for (int i = 0; i < size; i++) {
            fixedSeqListIndexMap.put(sequentialList.get(i).getIndex(),i);
        }
        this.endSeqIndex = Collections.max(fixedSeqListIndexMap.keySet());
    }

    private void initFullNameSeqMap() {
        Map<String, SequentialEntity> fullNameSeqMap = this.getFullNameSeqMap();
        if(fullNameSeqMap.size()>0){
            fullNameSeqMap.clear();
        }
        initChainFullName(chainEntity);
    }

    private void initChainFullName(RequestChainEntity chainEntity) {
        List<SequentialEntity> sequentialList = chainEntity.getSequentialList();
        for (SequentialEntity entity : sequentialList) {
            if(entity instanceof RequestChainEntity){
                initChainFullName((RequestChainEntity)entity);
            }else{
                this.fullNameSeqMap.put(entity.getFullName(),entity);
            }
        }
    }

    public Map<Integer, Integer> getFixedSeqListIndexMap() {
        if(fixedSeqListIndexMap==null){
            fixedSeqListIndexMap = new LinkedHashMap<>();
        }
        return fixedSeqListIndexMap;
    }


    public Map<String, SequentialEntity> getFullNameSeqMap() {
        if(fullNameSeqMap==null){
            fullNameSeqMap = new LinkedHashMap<>();
        }
        return fullNameSeqMap;
    }


    public Integer getEndSeqIndex() {
        return endSeqIndex;
    }

    public void setEndSeqIndex(Integer endSeqIndex) {
        this.endSeqIndex = endSeqIndex;
    }

    public RequestChainEntity getChainEntity() {
        return chainEntity;
    }
}

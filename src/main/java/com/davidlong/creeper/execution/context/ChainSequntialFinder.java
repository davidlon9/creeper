package com.davidlong.creeper.execution.context;

import com.davidlong.creeper.exception.RuntimeExecuteException;
import com.davidlong.creeper.execution.DynamicChainSwitcher;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.SequentialEntity;

import java.util.*;

public class ChainSequntialFinder extends DynamicChainSwitcher{
    private Map<Integer,Integer> fixedSeqListIndexMap;//当前chain的修复索引的映射
    private Map<String,SequentialEntity> fullNameSeqMap;//所有sequential的全名映射

    private Integer endSeqIndex=1;


    public ChainSequntialFinder(RequestChainEntity rootChain) {
        super(rootChain);
        fixedSeqListIndexMap = this.getFixedSeqListIndexMap();
        fullNameSeqMap = this.getFullNameSeqMap();
    }

    @Override
    public void initInternal() {
        initFullNameSeqMap();
        refreshExecutingIndexInfo();
    }

    @Override
    public void afterSwitched(RequestChainEntity switchedChain) {
        refreshExecutingIndexInfo();
    }

    private void refreshExecutingIndexInfo() {
        Map<Integer, Integer> fixedSeqListIndexMap = this.getFixedSeqListIndexMap();
        if(fixedSeqListIndexMap.size()>0){
            fixedSeqListIndexMap.clear();
        }
        List<SequentialEntity> sequentialList = super.getExecutingChain().getSequentialList();
        int size = sequentialList.size();
        for (int i = 0; i < size; i++) {
            fixedSeqListIndexMap.put(sequentialList.get(i).getIndex(),i);
        }
        this.endSeqIndex = Collections.max(fixedSeqListIndexMap.keySet()) + 1;
    }

    private void initFullNameSeqMap() {
    }

    public SequentialEntity findSeqByName(String name) {
        //如果fullname映射中找的得到
        SequentialEntity traget = fullNameSeqMap.get(name);
        if(traget==null){
            Set<Map.Entry<String, SequentialEntity>> entries = fullNameSeqMap.entrySet();
            for (Map.Entry<String, SequentialEntity> entry : entries) {
                String n = entry.getKey();
                if(n.endsWith("-"+name) || n.equals(name)){
                    if(traget!=null){
                        throw new RuntimeExecuteException("found duplicate sequential entity by abbrev name please try fullname");
                    }else{
                        traget=entry.getValue();
                    }
                }
            }
        }
        return traget;
    }

    public SequentialEntity findSeqByFixedIndex(int index) {
        Integer listIndex = getFixedSeqListIndexMap().get(index);
        if(listIndex==null && index < this.endSeqIndex){
            //如果找不到sequentialIndex，则使其自增1，去找它的下一个seqIndex对应的listIndex
            //例如:chain中seqs如下：1 3 4 5 6 7 10
            //执行forward时，会使seqIndex++，所以在执行1的forward时，下一seqIndex就是2，
            //当下次循环获取listIndex时，会找不到，因此会自增1，获取下一个seqIndex对应的listIndex是1
            return findSeqByFixedIndex(++index);
        }
        SequentialEntity target = null;
        if(listIndex!=null){
            target = getExecutingChain().getSequentialList().get(listIndex);
        }
        return target;
    }

    public SequentialEntity findFirstSeqByFixedIndex(){
        int firstIndex = getExecutingChain().getFirstSequential().getIndex();
        return findSeqByFixedIndex(firstIndex);
    }

    public SequentialEntity findNextSeq(int index) {
        return findSeqByFixedIndex(++index);
    }

    public RequestChainEntity getRootChain() {
        return rootChain;
    }

    public void setRootChain(RequestChainEntity rootChain) {
        this.rootChain = rootChain;
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
}

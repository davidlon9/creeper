package com.dlong.creeper.execution.context;

import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.model.ChainIndexInfo;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.SequentialEntity;

import java.util.*;

public class ChainIndexSequntialFinder {
    private Map<RequestChainEntity,ChainIndexInfo> chainIndexInfoMap = new LinkedHashMap<>();
    private RequestChainEntity rootChain;

    public ChainIndexSequntialFinder(RequestChainEntity rootChain) {
        this.rootChain = rootChain;
        initChainIndexInfoMap(rootChain);
    }

    private void initChainIndexInfoMap(RequestChainEntity rootChain) {
        this.chainIndexInfoMap.put(rootChain,new ChainIndexInfo(rootChain));

        List<SequentialEntity> sequentialList = rootChain.getSequentialList();
        for (SequentialEntity sequentialEntity : sequentialList) {
            if (sequentialEntity instanceof RequestChainEntity){
                initChainIndexInfoMap((RequestChainEntity) sequentialEntity);
            }else if(sequentialEntity instanceof RequestEntity){
                RequestEntity requestEntity = (RequestEntity) sequentialEntity;
                RequestChainEntity parent = requestEntity.getParent();
                if(parent != rootChain){
                    initChainIndexInfoMap(parent);
                }
            }
        }
    }

    public SequentialEntity findSeqByName(String name) {
        SequentialEntity seq = findSeqByName(name, rootChain);
        if(seq == null){
            List<SequentialEntity> sequentialList = rootChain.getSequentialList();
            for (SequentialEntity sequentialEntity : sequentialList) {
                if(sequentialEntity instanceof RequestChainEntity){
                    seq = findSeqByName(name, (RequestChainEntity) sequentialEntity);
                }
            }
        }
        return seq;
    }

    public SequentialEntity findSeqByName(String name, RequestChainEntity chain) {
        ChainIndexInfo chainIndexInfo = chainIndexInfoMap.get(chain);

        Map<String, SequentialEntity> fullNameSeqMap = chainIndexInfo.getFullNameSeqMap();
        SequentialEntity traget = fullNameSeqMap.get(name);
        if(traget == null){
            //如果fullname映射中找不到
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
        return findSeqByFixedIndex(index,rootChain);
    }

    public SequentialEntity findSeqByFixedIndex(int index,RequestChainEntity chain) {
        ChainIndexInfo chainIndexInfo = chainIndexInfoMap.get(chain);
        return findSeqByFixedIndex(index,chainIndexInfo);
    }


    private SequentialEntity findSeqByFixedIndex(int index,ChainIndexInfo chainIndexInfo) {
        Map<Integer, Integer> fixedSeqListIndexMap = chainIndexInfo.getFixedSeqListIndexMap();
        Integer endSeqIndex = chainIndexInfo.getEndSeqIndex();

        Integer listIndex = fixedSeqListIndexMap.get(index);
        if(listIndex==null && index < endSeqIndex){
            //如果找不到sequentialIndex，则使其自增1，去找它的下一个seqIndex对应的listIndex
            //例如:chain中seqs如下：1 3 4 5 6 7 10
            //执行forward时，会使seqIndex++，所以在执行1的forward时，下一seqIndex就是2，
            //当下次循环获取listIndex时，会找不到，因此会自增1，获取下一个seqIndex对应的listIndex是1
            return findSeqByFixedIndex(++index,chainIndexInfo);
        }
        SequentialEntity target = null;
        if(listIndex!=null){
            target = chainIndexInfo.getChainEntity().getSequentialList().get(listIndex);
        }
        return target;
    }

    public SequentialEntity findFirstSeqByFixedIndex(){
        return findFirstSeqByFixedIndex(rootChain);
    }

    public SequentialEntity findFirstSeqByFixedIndex(RequestChainEntity chain){
        int firstIndex = chain.getFirstSequential().getIndex();
        return findSeqByFixedIndex(firstIndex,chain);
    }

    public SequentialEntity findNextSeq(int index,ChainIndexInfo chainIndexInfo) {
        return findSeqByFixedIndex(++index,chainIndexInfo);
    }

    public SequentialEntity findNextSeq(SequentialEntity current) {
        RequestChainEntity parent = current.getParent();
        if(parent!=null){
            int index = current.getIndex();
            return findSeqByFixedIndex(++index,parent);
        }
        return null;
    }

    public boolean isLast(SequentialEntity entity){
        RequestChainEntity parent = entity.getParent();
        if(parent!=null){
            ChainIndexInfo chainIndexInfo = chainIndexInfoMap.get(parent);
            return entity.getIndex()==chainIndexInfo.getEndSeqIndex();
        }
        return true;
    }

}

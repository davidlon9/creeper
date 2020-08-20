package com.davidlong.demo.traiker;

import com.davidlong.creeper.exception.RuntimeExecuteException;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.RequestEntity;
import com.davidlong.creeper.model.seq.SequentialEntity;

import java.util.*;

public class Think {
    Map<String,SequentialEntity> fullNameSeqsMap=new HashMap<>();

    List<SequentialEntity> sequentialList=new ArrayList<>();

    Map<Integer,Integer> fixedListIndexMap =new LinkedHashMap<>();
    //1 0
    //3 1

    final int defualtFirstIndex=1;

    //jump name
    //1.name : 全局查找，如果找到一个则返回，如果是多个则抛出异常
    //2.name1-name2 : 全路径查找，一定要写全，适用于两个chain内有同一个name的情况
    public void thinkNextSeq(){
        Integer endSeqIndex = Collections.max(fixedListIndexMap.keySet()) + 1;
        Integer endListIndex = sequentialList.size()-1;

        Integer listIndex;

        int currSeqIndex=defualtFirstIndex;
        //只要不是EndSeqIndex就一直执行
        while (currSeqIndex < endSeqIndex){
            listIndex = fixedListIndexMap.get(currSeqIndex);
            if(listIndex==null && currSeqIndex<endSeqIndex){
                //如果找不到sequentialIndex，则使其自增1，去找它的下一个seqIndex对应的listIndex
                //例如:chain中seqs如下：1 3 4 5 6 7 10
                //执行forward时，会使seqIndex++，所以在执行1的forward时，下一seqIndex就是2，
                //当下次循环获取listIndex时，会找不到，因此会自增1，获取下一个seqIndex对应的listIndex是1
                currSeqIndex++;
                continue;
            }

            SequentialEntity sequentialEntity = sequentialList.get(listIndex);

            if(sequentialEntity==null){
                throw new RuntimeExecuteException("Chain中找不到对应索引的Sequential");
            }

            Object result = loopExecuteRequest(sequentialEntity);

            if(result instanceof Integer){
                currSeqIndex = (Integer)result;
            }else if(result instanceof String){
                //如果fullname映射中找的得到
                SequentialEntity seq = fullNameSeqsMap.get(result);
                //如果在当前chain中则跳到该seq对应的index
                if(sequentialList.contains(seq)){
                    currSeqIndex = seq.getIndex();
                }else{
                    //如果在外部
                    if(seq instanceof RequestChainEntity){
                        //是chain的话，直接执行该chain
                        executeChain();
                    }else if(seq instanceof RequestEntity){
                        //是request的话，执行该chain,并传递firstIndex为该seq的index
                        executeChain(seq.getIndex());
                    }
                    //由于转到外部了，所以跳出该chain执行
                    break;
                }
            }
        }
    }

    //jump continue back forward 都可以作用于chain或request
    //restart terminate 作用于chain
    //retry 作用于requst

    public Object loopExecuteRequest(SequentialEntity sequentialEntity){
        while(true){
            Object o = executeRequest(sequentialEntity);
            //返回Retry则继续循环
            //返回continue则跳出当前循环，继续下个循环，用name指定继续哪个循环，默认当前循环
            //所有其他策略都表示跳出当前循环
            break;
        }
        return sequentialEntity.getIndex()+1;
    }

    public Object loopExecuteChain(){
        while(true){
            Object o = executeChain();
            //返回Restart则继续循环
            //返回continue则跳出当前循环，继续下个循环，用name指定继续哪个循环，默认当前循环
            //所有其他策略都表示跳出当前循环

            break;
        }
        return null;
    }

    public Object executeRequest(SequentialEntity sequentialEntity){
        return sequentialEntity.getIndex()+1;
    }

    public Object executeChain(){
        return null;
    }

    //执行并传递，起始seqIndex
    public Object executeChain(Integer firstSeqIndex){
        return null;
    }

    public static void main(String[] args) {
        int a=1;
        do {
            if(a<5){
                a++;
                System.out.println(a);
                continue;
            }
            System.out.println("a");
        }while (a<3);
    }
}

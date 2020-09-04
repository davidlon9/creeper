package com.dlong.creeper.execution.context;

import com.dlong.creeper.model.Param;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import java.util.List;

public class ChainContext extends ExecutionContext implements Cloneable{
    private static Logger logger = Logger.getLogger(ChainContext.class);

    private RequestChainEntity rootChain;

    private ChainIndexSequntialFinder sequntialFinder;

    public ChainContext(RequestChainEntity rootChain) {
        this(rootChain,null);
    }

    public ChainContext(RequestChainEntity rootChain, ChainContext context) {
        super(context);
        this.rootChain = rootChain;
        //初始化Chain实体的索引信息
        this.sequntialFinder = new ChainIndexSequntialFinder(this.rootChain);
        initDefaultValueParams();
    }

    public RequestChainEntity getRootChain() {
        return rootChain;
    }

    private void initDefaultValueParams() {
        List<SequentialEntity> sequentialList = rootChain.getSequentialList();
        for (SequentialEntity seq : sequentialList) {
            if (seq instanceof RequestEntity){
                List<Param> params = ((RequestEntity) seq).getRequestInfo().getParams();
                for (Param param : params) {
                    paramStore.addIfNull(param);
                }
            }
        }
    }

    public RequestEntity getRequestEntity(String name){
        SequentialEntity seq = getSequntialFinder().findSeqByName(name);
        if(seq instanceof RequestEntity){
            RequestEntity requestEntity = (RequestEntity) seq;
            requestEntity.buildRequest(this);
            return requestEntity;
        }
        return null;
    }

    public Request getRequest(String name){
        RequestEntity requestEntity = getRequestEntity(name);
        if (requestEntity != null) {
            return requestEntity.getRequest();
        }
        return null;
    }

    public ChainIndexSequntialFinder getSequntialFinder() {
        return sequntialFinder;
    }

    @Override
    public ChainContext clone() throws CloneNotSupportedException {
        ChainContext clone = (ChainContext) super.clone();
        clone.setContextStore(this.contextStore.clone());
        return clone;
    }
}

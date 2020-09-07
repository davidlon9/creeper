package com.dlong.creeper.execution;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.base.ChainExecutor;
import com.dlong.creeper.execution.base.ContextExecutor;
import com.dlong.creeper.execution.base.RequestExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ChainExecutionResult;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import com.dlong.creeper.resolver.ChainsMappingResolver;
import org.springframework.util.Assert;

import java.io.IOException;

public class ChainContextExecutor implements ContextExecutor {
    private ChainContext chainContext;

    public ChainContextExecutor(ChainContext context) {
        this.chainContext = context;
    }

    public ChainContextExecutor(Class entityClass) {
        RequestChainEntity chainEntity = new ChainsMappingResolver().resolve(entityClass);
        this.chainContext = new ChainContext(chainEntity);
    }

    @Override
    public ExecutionResult exeucteRequest(String name) {
        SequentialEntity seq = chainContext.getSequntialFinder().findSeqByName(name);
        Assert.isInstanceOf(RequestEntity.class,seq);
        RequestEntity requestEntity = (RequestEntity) seq;
        RequestExecutor<RequestEntity> requestExecutor = ExecutorFactory.createRequestExecutor(requestEntity.getClass(), chainContext);
        try {
            return requestExecutor.execute(requestEntity);
        } catch (IOException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ChainExecutionResult exeucteChain(String name) {
        SequentialEntity seq = chainContext.getSequntialFinder().findSeqByName(name);
        Assert.isInstanceOf(RequestChainEntity.class,seq);
        RequestChainEntity chainEntity = (RequestChainEntity) seq;
        ChainExecutor chainExecutor = ExecutorFactory.createChainExecutor(chainEntity.getClass(), chainContext);
        try {
            return chainExecutor.execute(chainEntity);
        } catch (IOException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ChainExecutionResult exeucteRootChain() {
        ChainExecutor executor = ExecutorFactory.createChainExecutor(chainContext.getRootChain().getClass(), chainContext);
        return executor.execute();
    }

    @Override
    public ChainContext getChainContext() {
        return chainContext;
    }


}

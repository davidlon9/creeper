package com.davidlong.demo.tbk;

import com.davidlong.creeper.annotation.Host;
import com.davidlong.creeper.annotation.RequestLog;
import com.davidlong.creeper.annotation.seq.RequestChain;
import com.davidlong.creeper.execution.RequestChainExecutor;
import com.davidlong.creeper.execution.base.ChainExecutor;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.resolver.ChainsMappingResolver;

@Host(value = "pub.alimama.com" , scheme = "https")
@RequestChain(index = 1)
@RequestLog(showFilledParams = false,showFilledHeaders = false)
public class TBKFavsChain {

    @Host(value ="www.alimama.com", scheme = "https")
    @RequestChain(index = 1)
    public class TBKLoginChain{

    }

    public static void main(String[] args) {
        RequestChainEntity chainEntity = new ChainsMappingResolver(TBKFavsChain.class).resolve();
        ExecutionContext context = new ExecutionContext(chainEntity);

        ChainExecutor executor = new RequestChainExecutor(context);
        ExecutionResult execute = executor.execute();
        System.out.println();
    }
}

package com.davidlong.tbk;

import com.davidlong.http.annotation.Host;
import com.davidlong.http.annotation.RequestLog;
import com.davidlong.http.annotation.seq.RequestChain;
import com.davidlong.http.execution.RequestChainExecutor;
import com.davidlong.http.execution.base.ChainExecutor;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.resolver.ChainsMappingResolver;

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

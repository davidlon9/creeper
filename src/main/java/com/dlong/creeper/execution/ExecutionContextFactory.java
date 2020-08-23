package com.dlong.creeper.execution;

import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.Param;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.resolver.ChainsMappingResolver;

import java.util.List;

public class ExecutionContextFactory {
    public static ExecutionContext createContext(Class<?> entityClass, List<Param> startParams, boolean fixIndex){
        RequestChainEntity chainEntity = new ChainsMappingResolver(entityClass,fixIndex).resolve();
        return new ExecutionContext(chainEntity,startParams);
    }

    public static ExecutionContext createContext(Class<?> entityClass, boolean fixIndex){
        return createContext(entityClass,null,fixIndex);
    }

    public static ExecutionContext createContext(Class<?> entityClass, List<Param> startParams){
        return createContext(entityClass,startParams,true);
    }

    public static ExecutionContext createContext(Class<?> entityClass){
        return createContext(entityClass,null);
    }


}

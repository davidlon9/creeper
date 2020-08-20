package com.davidlong.creeper.execution;

import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.Param;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.resolver.ChainsMappingResolver;

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

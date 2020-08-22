package com.davidlong.creeper.execution.resolver;

import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.resolver.method.HandlerMethodResultResolver;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.SequentialEntity;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;

public abstract class BaseExecutionResultResolver implements ExecutionResultResolver {
    protected Logger logger=Logger.getLogger(SimpleExecutionResultResolver.class);

    protected Class<?> findResolverClass(Map<Class<?>,HandlerMethodResultResolver> resolversMap, Object obj) {
        Set<Class<?>> classes = resolversMap.keySet();
        Class<?> oClz = obj.getClass();
        Class<?> resolverClass=null;
        for (Class<?> clz : classes) {
            if (clz.isAssignableFrom(oClz)) {
                resolverClass=clz;
            }
        }
        return resolverClass;
    }

    protected void handleNoNext(ExecutionResult executionResult, ExecutionContext context){
        if(executionResult.getNextSeq() == null){
            SequentialEntity orginalSeq = executionResult.getOrginalSeq();
            if(context.getSequntialFinder().isLast(orginalSeq)){
                logger.info("Resolver resolve over all sequential entity in "+orginalSeq.getParent());
            }else{
                logger.warn("Resolver did't resolve out next sequential entity");
            }
        }
    }
}
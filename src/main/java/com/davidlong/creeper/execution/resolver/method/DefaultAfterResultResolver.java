package com.davidlong.creeper.execution.resolver.method;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ExecutionResult;
import org.apache.log4j.Logger;

public class DefaultAfterResultResolver implements HandlerMethodResultResolver {
    private static Logger logger=Logger.getLogger(DefaultAfterResultResolver.class);

    private HandlerMethodResultResolver defualtResolver;

    public DefaultAfterResultResolver() {
        this.defualtResolver = new DefaultForwardAfterResultResolver();
    }

    public DefaultAfterResultResolver(HandlerMethodResultResolver defualtResolver) {
        this.defualtResolver = defualtResolver;
    }

    @Override
    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context, Object methodResult) throws ExecutionException {
        return this.defualtResolver.resolveResult(executionResult,context,methodResult);
    }

//默认继续前进
//    @Override
//    public ExecutionResult resolveResult(ExecutionResult executionResult, ExecutionContext context,Object methodResult) throws ExecutionException {
//        SequentialEntity next=null;
//        if(methodResult == null || (methodResult instanceof Boolean && !(Boolean)methodResult)){
//            int orginalIndex = executionResult.getOrginalIndex();
//            next = context.getSequntialFinder().findSeqByFixedIndex(++orginalIndex,executionResult.getOrginalParent());
//        }
//        executionResult.setNextSeq(next);
//        return executionResult;
//    }
}

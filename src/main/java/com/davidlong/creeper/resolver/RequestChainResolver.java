package com.davidlong.creeper.resolver;

import com.davidlong.creeper.annotation.seq.RequestChain;
import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.resolver.base.BaseChainResolver;

public class RequestChainResolver extends BaseChainResolver implements ChainResolver{
    public RequestChainResolver(Class<?> handleClass) {
        super(handleClass, RequestChain.class);
    }

    @Override
    public RequestChainEntity resolveChain() {
        try {
            return super.resolveChain(new RequestChainEntity());
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

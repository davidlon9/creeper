package com.dlong.creeper.resolver;

import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.resolver.base.BaseChainAnnoResolver;

public class RequestChainResolver extends BaseChainAnnoResolver implements ChainAnnoResolver {
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

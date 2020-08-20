package com.davidlong.http.resolver;

import com.davidlong.http.annotation.seq.RequestChain;
import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.resolver.base.BaseChainResolver;

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

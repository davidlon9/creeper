package com.davidlong.http.resolver;

import com.davidlong.http.annotation.seq.multi.MultiRequestChain;
import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.multi.MultiRequestChainEntity;
import com.davidlong.http.resolver.base.BaseChainResolver;

public class MultiRequestChainResolver extends BaseChainResolver implements ChainResolver{
    public MultiRequestChainResolver(Class<?> handleClass) {
        super(handleClass, MultiRequestChain.class);
    }

    public MultiRequestChainEntity resolveChain() {
        try {
            MultiRequestChainEntity entity=new MultiRequestChainEntity();
            super.resolveChain(entity);
            MultiRequestChain multiRequestChain = (MultiRequestChain) super.getAnnotation();
            entity.setThreadSize(multiRequestChain.threadSize());
            entity.setInterval(multiRequestChain.interval());
            entity.setMoveStopAll(multiRequestChain.moveStopAll());
            entity.setShareContext(multiRequestChain.shareContext());

            return entity;
        } catch (AnnotationNotFoundException e) {
            return null;
        }
    }
}

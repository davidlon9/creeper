package com.dlong.creeper.resolver;

import com.dlong.creeper.annotation.seq.multi.MultiRequestChain;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.multi.MultiRequestChainEntity;
import com.dlong.creeper.resolver.base.BaseChainAnnoResolver;

public class MultiRequestChainResolver extends BaseChainAnnoResolver implements ChainAnnoResolver {
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

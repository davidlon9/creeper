package com.dlong.creeper.resolver;

import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.RequestInfo;
import com.dlong.creeper.model.seq.SeqRequestEntity;
import com.dlong.creeper.resolver.base.BaseRequestAnnoResolver;

import java.lang.reflect.AnnotatedElement;

public class SeqRequestResolver extends BaseRequestAnnoResolver implements RequestResolver{
    public SeqRequestResolver(Class<?> handleClass,AnnotatedElement handler) {
        super(handleClass,handler,SeqRequest.class);
    }

    @Override
    public SeqRequestEntity resolve(RequestInfo requestInfo) {
        try {
            SeqRequestEntity seqRequestEntity = new SeqRequestEntity();
            super.resolveRequest(requestInfo,seqRequestEntity);
            return seqRequestEntity;
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

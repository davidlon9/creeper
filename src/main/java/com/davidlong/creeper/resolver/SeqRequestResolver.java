package com.davidlong.creeper.resolver;

import com.davidlong.creeper.annotation.seq.SeqRequest;
import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.RequestInfo;
import com.davidlong.creeper.model.seq.SeqRequestEntity;
import com.davidlong.creeper.resolver.base.BaseRequestResolver;

import java.lang.reflect.AnnotatedElement;

public class SeqRequestResolver extends BaseRequestResolver implements RequestResolver{
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

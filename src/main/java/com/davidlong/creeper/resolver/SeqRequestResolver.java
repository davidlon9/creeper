package com.davidlong.http.resolver;

import com.davidlong.http.annotation.seq.SeqRequest;
import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.RequestInfo;
import com.davidlong.http.model.seq.SeqRequestEntity;
import com.davidlong.http.resolver.base.BaseRequestResolver;

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

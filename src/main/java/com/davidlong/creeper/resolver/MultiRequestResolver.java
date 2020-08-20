package com.davidlong.creeper.resolver;

import com.davidlong.creeper.annotation.seq.multi.MultiRequest;
import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.RequestInfo;
import com.davidlong.creeper.model.seq.multi.MultiRequestEntity;
import com.davidlong.creeper.resolver.base.BaseRequestResolver;

import java.lang.reflect.AnnotatedElement;

public class MultiRequestResolver extends BaseRequestResolver implements RequestResolver{
    public MultiRequestResolver(Class<?> handleClass,AnnotatedElement handler) {
        super(handleClass,handler,MultiRequest.class);
    }

    public MultiRequestEntity resolve(RequestInfo requestInfo) {
        MultiRequestEntity request = new MultiRequestEntity();
        try {
            super.resolveRequest(requestInfo,request);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        MultiRequest multiRequest = (MultiRequest) super.getAnnotation();
        request.setThreadSize(multiRequest.threadSize());
        request.setMoveStopAll(multiRequest.moveStopAll());
        request.setShareContext(multiRequest.shareContext());
        return request;
    }
}

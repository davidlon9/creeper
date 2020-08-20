package com.davidlong.http.resolver;

import com.davidlong.http.annotation.seq.multi.MultiRequestQueue;
import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.RequestInfo;
import com.davidlong.http.model.seq.multi.MultiRequestEntity;
import com.davidlong.http.model.seq.multi.MultiRequestQueueEntity;
import com.davidlong.http.resolver.base.BaseRequestResolver;

import java.lang.reflect.AnnotatedElement;

public class MultiRequestQueueResolver extends BaseRequestResolver implements RequestResolver{
    public MultiRequestQueueResolver(Class<?> handleClass,AnnotatedElement handler) {
        super(handleClass,handler,MultiRequestQueue.class);
    }

    public MultiRequestEntity resolve(RequestInfo requestInfo) {
        MultiRequestQueueEntity request = new MultiRequestQueueEntity();
        try {
            super.resolveRequest(requestInfo,request);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        MultiRequestQueue multiRequestQueue = (MultiRequestQueue) super.getAnnotation();
        request.setThreadSize(multiRequestQueue.threadSize());
        request.setMoveStopAll(multiRequestQueue.moveStopAll());
        request.setShareContext(multiRequestQueue.shareContext());

        request.setDelay(multiRequestQueue.delay());
        request.setQueueContextKey(multiRequestQueue.queueContextKey());
        request.setQueueElementKey(multiRequestQueue.queueElementKey());
        request.setStopConditionExpr(multiRequestQueue.stopConditionExpr());
        return request;
    }
}

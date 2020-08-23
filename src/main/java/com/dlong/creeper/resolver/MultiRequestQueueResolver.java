package com.dlong.creeper.resolver;

import com.dlong.creeper.annotation.seq.multi.MultiRequestQueue;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.RequestInfo;
import com.dlong.creeper.model.seq.multi.MultiRequestEntity;
import com.dlong.creeper.model.seq.multi.MultiRequestQueueEntity;
import com.dlong.creeper.resolver.base.BaseRequestResolver;

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

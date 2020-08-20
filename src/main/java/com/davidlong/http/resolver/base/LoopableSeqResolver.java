package com.davidlong.http.resolver.base;

import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.LoopableEntity;
import com.davidlong.http.model.seq.control.Looper;
import com.davidlong.http.model.seq.recorder.UrlRecorder;
import com.davidlong.http.resolver.ResolverFactory;
import com.davidlong.http.resolver.looper.LooperResolver;
import com.davidlong.http.resolver.recorder.RecorderResolver;
import com.davidlong.http.resolver.util.WrapUtil;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class LoopableSeqResolver extends HandleableResolver{
    private Logger logger = Logger.getLogger(LoopableSeqResolver.class);

    public LoopableSeqResolver(Class<?> handleClass, AnnotatedElement target, Class<? extends Annotation> annoClass) {
        super(handleClass, target, annoClass);
    }

    public LoopableEntity resolveLoopable(LoopableEntity loopableEntity) throws AnnotationNotFoundException {
        super.resolveHandleable(loopableEntity);
        resovleLooper(loopableEntity);
        resolveRecoder(loopableEntity);
        return loopableEntity;
    }

    private void resolveRecoder(LoopableEntity loopableEntity) throws AnnotationNotFoundException {
        RecorderResolver recoderResolver = ResolverFactory.getRecoderResolver(getTarget());
        if(recoderResolver!=null){
            UrlRecorder recorder = recoderResolver.resolve();
            if(recorder != null){
                loopableEntity.setRecorder(recorder);
                logger.info("set Recorder "+recorder.getClass().getSimpleName()+" for "+WrapUtil.enBrackets(loopableEntity.getName()));
            }
        }
    }

    private void resovleLooper(LoopableEntity loopableEntity) throws AnnotationNotFoundException {
        LooperResolver looperResolver = ResolverFactory.getLooperResolver(getTarget());
        if(looperResolver!=null){
            Looper looper = looperResolver.resolve();
            loopableEntity.setLooper(looper);
            logger.info("set Looper "+looper.getClass().getSimpleName()+" for "+WrapUtil.enBrackets(loopableEntity.getName()));
        }
    }
}

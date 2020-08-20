package com.davidlong.creeper.resolver.base;

import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.LoopableEntity;
import com.davidlong.creeper.model.seq.control.Looper;
import com.davidlong.creeper.model.seq.recorder.UrlRecorder;
import com.davidlong.creeper.resolver.ResolverFactory;
import com.davidlong.creeper.resolver.looper.LooperResolver;
import com.davidlong.creeper.resolver.recorder.RecorderResolver;
import com.davidlong.creeper.resolver.util.WrapUtil;
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

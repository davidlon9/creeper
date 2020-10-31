package com.dlong.creeper.resolver.base;

import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import com.dlong.creeper.resolver.ResolverFactory;
import com.dlong.creeper.resolver.looper.LooperResolver;
import com.dlong.creeper.resolver.recorder.RecorderResolver;
import com.dlong.creeper.resolver.util.WrapUtil;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class LoopableSeqResolver extends HandleableResolver{
    private static Logger logger = Logger.getLogger(LoopableSeqResolver.class);

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
            UrlRecorder urlRecorder = recoderResolver.resolve();
            if(urlRecorder != null){
                loopableEntity.setRecorder(urlRecorder);
                logger.info("set UrlRecorder "+ urlRecorder.getClass().getSimpleName()+" for "+WrapUtil.enBrackets(loopableEntity.getName()));
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

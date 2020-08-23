package com.dlong.creeper.resolver.looper;

import com.dlong.creeper.annotation.control.ExecutionMode;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.resolver.base.AnnotationResolver;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public abstract class BaseLooperResolver<T extends Looper> extends AnnotationResolver implements LooperResolver {
    public BaseLooperResolver(AnnotatedElement target, Class<? extends Annotation> annoClass) {
        super(target,annoClass);
    }

    public T resolve(T looper) throws AnnotationNotFoundException{
        super.resolveAnnotation();
        Map<String,Object> annotationAttrs = AnnotationUtils.getAnnotationAttributes(getAnnotation());

        ExecutionMode executionMode = (ExecutionMode) annotationAttrs.get("executionMode");
        looper.setExecutionMode(executionMode);
        return looper;
    }

}

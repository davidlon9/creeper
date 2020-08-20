package com.davidlong.http.resolver.looper;

import com.davidlong.http.annotation.control.ExecutionMode;
import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.control.Looper;
import com.davidlong.http.resolver.base.AnnotationResolver;
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

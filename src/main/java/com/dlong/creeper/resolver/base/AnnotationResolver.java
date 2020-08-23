package com.dlong.creeper.resolver.base;

import com.dlong.creeper.exception.AnnotationNotFoundException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationResolver{
    private Class<? extends Annotation> annoClass;
    private Annotation annotation;
    private AnnotatedElement target;

    public AnnotationResolver(AnnotatedElement target, Class<? extends Annotation> annoClass) {
        this.annoClass = annoClass;
        this.target = target;
    }

    public Annotation resolveAnnotation() throws AnnotationNotFoundException{
        return annotation = AnnotationUtils.getAnnotation(target, annoClass);
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Class<? extends Annotation> getAnnoClass() {
        return annoClass;
    }

    public AnnotatedElement getTarget() {
        return target;
    }

    public void setTarget(AnnotatedElement target) {
        this.target = target;
    }
}

package com.davidlong.creeper.resolver.looper;

import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.control.Looper;

import java.lang.annotation.Annotation;

public interface LooperResolver {
    Looper resolve() throws AnnotationNotFoundException;
    Annotation getAnnotation();
}

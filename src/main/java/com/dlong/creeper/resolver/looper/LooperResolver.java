package com.dlong.creeper.resolver.looper;

import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.control.Looper;

import java.lang.annotation.Annotation;

public interface LooperResolver {
    Looper resolve() throws AnnotationNotFoundException;
    Annotation getAnnotation();
}

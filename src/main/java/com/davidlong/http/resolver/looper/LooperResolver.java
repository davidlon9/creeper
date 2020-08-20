package com.davidlong.http.resolver.looper;

import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.control.Looper;

import java.lang.annotation.Annotation;

public interface LooperResolver {
    Looper resolve() throws AnnotationNotFoundException;
    Annotation getAnnotation();
}

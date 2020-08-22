package com.davidlong.creeper.resolver;

import com.davidlong.creeper.model.seq.RequestInfo;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public interface RequestInfoResolver {
    Map<AnnotatedElement,RequestInfo> resolve(Class<?> chainClass);
}

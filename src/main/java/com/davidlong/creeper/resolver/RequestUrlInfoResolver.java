package com.davidlong.http.resolver;

import com.davidlong.http.model.seq.RequestInfo;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public interface RequestUrlInfoResolver {
    Map<AnnotatedElement,RequestInfo> resolve(Class<?> chainClass);
}

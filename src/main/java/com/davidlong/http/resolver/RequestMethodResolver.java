package com.davidlong.http.resolver;

import com.davidlong.http.model.seq.RequestEntity;

import java.lang.reflect.Method;
import java.util.Map;

public interface RequestMethodResolver {
    RequestEntity resolveRequest(Map<Method, String> methodUrlMap, Method method);
}

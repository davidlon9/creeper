package com.davidlong.creeper.resolver;

import com.davidlong.creeper.model.seq.RequestEntity;

import java.lang.reflect.Method;
import java.util.Map;

public interface RequestMethodResolver {
    RequestEntity resolveRequest(Map<Method, String> methodUrlMap, Method method);
}

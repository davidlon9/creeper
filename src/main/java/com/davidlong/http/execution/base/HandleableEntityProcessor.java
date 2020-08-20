package com.davidlong.http.execution.base;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.model.seq.HandleableEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

import java.lang.reflect.InvocationTargetException;

public interface HandleableEntityProcessor {
    Object beforeExecute(HandleableEntity handleableEntity, Request request) throws InvocationTargetException, IllegalAccessException, ExecutionException;
    Object afterExecute(HandleableEntity handleableEntity,HttpResponse response) throws InvocationTargetException, IllegalAccessException;
}

package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.model.seq.HandleableEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

import java.lang.reflect.InvocationTargetException;

public interface HandleableEntityProcessor {
    Object beforeExecute(HandleableEntity handleableEntity, Request request) throws InvocationTargetException, IllegalAccessException, ExecutionException;
    Object afterExecute(HandleableEntity handleableEntity,HttpResponse response) throws InvocationTargetException, IllegalAccessException;
}

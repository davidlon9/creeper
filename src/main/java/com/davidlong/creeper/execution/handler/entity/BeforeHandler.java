package com.davidlong.creeper.execution.handler.entity;

import com.davidlong.creeper.execution.context.ExecutionContext;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public interface BeforeHandler extends SequentialHandler {
    Object beforeHandle(Request request, ExecutionContext context) throws IOException;
}

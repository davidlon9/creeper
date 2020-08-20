package com.davidlong.creeper.execution.handler.entity;

import com.davidlong.creeper.execution.context.ExecutionContext;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface AfterHandler extends SequentialHandler {
    Object afterHandle(HttpResponse response, ExecutionContext context) throws IOException;
}

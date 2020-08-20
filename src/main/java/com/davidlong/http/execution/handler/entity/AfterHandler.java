package com.davidlong.http.execution.handler.entity;

import com.davidlong.http.execution.context.ExecutionContext;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface AfterHandler extends SequentialHandler {
    Object afterHandle(HttpResponse response, ExecutionContext context) throws IOException;
}

package com.davidlong.http.execution.handler.entity;

import com.davidlong.http.execution.context.ExecutionContext;

import java.io.IOException;

public interface ChainAfterHandler extends SequentialHandler {
    Object afterHandle(ExecutionContext context) throws IOException;
}

package com.davidlong.http.execution.handler.entity;

import com.davidlong.http.execution.context.ExecutionContext;

import java.io.IOException;

public interface ChainBeforeHandler extends SequentialHandler {
    Object beforeHandle(ExecutionContext context) throws IOException;
}

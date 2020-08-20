package com.davidlong.creeper.execution.handler.entity;

import com.davidlong.creeper.execution.context.ExecutionContext;

import java.io.IOException;

public interface ChainAfterHandler extends SequentialHandler {
    Object afterHandle(ExecutionContext context) throws IOException;
}

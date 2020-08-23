package com.dlong.creeper.execution.handler.entity;

import com.dlong.creeper.execution.context.ExecutionContext;

import java.io.IOException;

public interface ChainAfterHandler extends SequentialHandler {
    Object afterHandle(ExecutionContext context) throws IOException;
}

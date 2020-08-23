package com.dlong.creeper.execution.handler.entity;

import com.dlong.creeper.execution.context.ExecutionContext;

import java.io.IOException;

public interface ChainBeforeHandler extends SequentialHandler {
    Object beforeHandle(ExecutionContext context) throws IOException;
}

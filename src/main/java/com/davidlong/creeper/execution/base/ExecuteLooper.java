package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.model.LoopExecutionResult;
import com.davidlong.creeper.model.seq.LoopableEntity;

import java.io.IOException;

public interface ExecuteLooper<T extends LoopableEntity> {
    LoopExecutionResult<T> loop(T t) throws ExecutionException, IOException;
}

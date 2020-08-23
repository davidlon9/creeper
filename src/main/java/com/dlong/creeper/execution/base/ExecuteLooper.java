package com.dlong.creeper.execution.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;

import java.io.IOException;

public interface ExecuteLooper<T extends LoopableEntity> {
    LoopExecutionResult<T> loop(T t) throws ExecutionException, IOException;
}

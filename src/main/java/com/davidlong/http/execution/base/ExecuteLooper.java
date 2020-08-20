package com.davidlong.http.execution.base;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.model.LoopExecutionResult;
import com.davidlong.http.model.seq.LoopableEntity;

import java.io.IOException;

public interface ExecuteLooper<T extends LoopableEntity> {
    LoopExecutionResult<T> loop(T t) throws ExecutionException, IOException;
}

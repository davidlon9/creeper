package com.dlong.creeper.execution.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestEntity;

import java.io.IOException;

public interface RequestExecutor<T extends RequestEntity> extends SequentialExecutor<T>{
    ExecutionResult<T> exeucteRequest(RequestEntity requestEntity,ExecutionResult<T> executionResult) throws ExecutionException, IOException;
}

package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.SequentialEntity;

import java.io.IOException;


public interface SequentialExecutor<T extends SequentialEntity>{
    /**
     * 执行任意类型的子SequentialEntity
     * @param t
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    ExecutionResult<T> execute(T t) throws IOException, ExecutionException;
}

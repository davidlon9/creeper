package com.dlong.creeper.execution.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.SequentialEntity;

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

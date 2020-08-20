package com.davidlong.http.execution.base;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.SequentialEntity;

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

package com.dlong.creeper.execution.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;

import java.io.IOException;

public interface LoopableExecutor<T extends LoopableEntity> extends SequentialExecutor<T>{

    /**
     * 实际执行的操作，与循环执行区分
     * @param t
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    ExecutionResult<T> doExecute(T t) throws IOException, ExecutionException;
}

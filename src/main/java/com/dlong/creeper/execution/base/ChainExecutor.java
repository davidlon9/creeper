package com.dlong.creeper.execution.base;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.model.result.ChainExecutionResult;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.SequentialEntity;

import java.io.IOException;

public interface ChainExecutor<T extends RequestChainEntity> extends SequentialExecutor<T> {
    /**
     * 执行context中，当前处理中的Chain，默认是根Chain
     * @return
     */
    ExecutionResult<T> execute();

    @Override
    ExecutionResult<T> execute(T t) throws IOException, ExecutionException;

    /**
     * 执行单个SequentialEntity
     * @param sequentialEntity
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    ExecutionResult executeSeqEntity(ChainExecutionResult<T> executionResult, SequentialEntity sequentialEntity) throws IOException, ExecutionException;

    /**
     * 执行RequestEntity实体
     * @param requestEntity
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    <T extends RequestEntity> ExecutionResult<RequestEntity> executeRequest(T requestEntity) throws IOException, ExecutionException;
}

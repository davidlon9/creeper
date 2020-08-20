package com.davidlong.http.execution.base;
import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.model.ChainExecutionResult;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.model.seq.RequestEntity;
import com.davidlong.http.model.seq.SequentialEntity;

import java.io.IOException;

public interface ChainExecutor<T extends RequestChainEntity> extends SequentialExecutor<T>{
    /**
     * 执行context中，当前处理中的Chain，默认是根Chain
     * @return
     */
    ChainExecutionResult<T> execute();

    @Override
    ChainExecutionResult<T> execute(T t) throws IOException, ExecutionException;

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

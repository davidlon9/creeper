package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.model.seq.RequestEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface RequestExecutor<T extends RequestEntity> extends SequentialExecutor<T>{
    HttpResponse exeucteRequest(RequestEntity requestEntity) throws ExecutionException, IOException;
}

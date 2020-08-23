package com.dlong.creeper.execution.base;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.model.seq.RequestEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface RequestExecutor<T extends RequestEntity> extends SequentialExecutor<T>{
    HttpResponse exeucteRequest(RequestEntity requestEntity) throws ExecutionException, IOException;
}

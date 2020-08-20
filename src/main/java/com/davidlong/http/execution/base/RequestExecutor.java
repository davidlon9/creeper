package com.davidlong.http.execution.base;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.model.seq.RequestEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface RequestExecutor<T extends RequestEntity> extends SequentialExecutor<T>{
    HttpResponse exeucteRequest(RequestEntity requestEntity) throws ExecutionException, IOException;
}

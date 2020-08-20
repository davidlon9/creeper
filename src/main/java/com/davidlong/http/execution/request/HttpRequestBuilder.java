package com.davidlong.http.execution.request;

import com.davidlong.http.model.seq.RequestEntity;
import com.davidlong.http.model.seq.RequestInfo;
import org.apache.http.client.fluent.Request;

public interface HttpRequestBuilder{
    Request buildRequest(RequestEntity requestEntity);
}

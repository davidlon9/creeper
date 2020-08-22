package com.davidlong.creeper.execution.request;

import com.davidlong.creeper.model.seq.RequestInfo;
import org.apache.http.client.fluent.Request;

public interface HttpRequestBuilder{
    Request buildRequest(RequestInfo requestInfo);
}

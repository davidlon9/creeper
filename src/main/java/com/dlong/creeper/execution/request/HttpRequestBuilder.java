package com.dlong.creeper.execution.request;

import com.dlong.creeper.model.seq.RequestInfo;
import org.apache.http.client.fluent.Request;

public interface HttpRequestBuilder{
    Request buildRequest(RequestInfo requestInfo);

    String buildUrl(RequestInfo requestInfo);
}

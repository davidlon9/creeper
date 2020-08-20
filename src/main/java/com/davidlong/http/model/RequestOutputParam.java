package com.davidlong.http.model;

import com.davidlong.http.model.seq.RequestEntity;
import org.apache.http.HttpResponse;

public class RequestOutputParam extends ExecutionOutputParam<RequestEntity>{
    private HttpResponse httpResponse;

    public RequestOutputParam() {
    }

    public RequestOutputParam(RequestEntity entity, HttpResponse httpResponse) {
        super(entity);
        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }
}

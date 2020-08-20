package com.davidlong.http.model;

import com.davidlong.http.model.seq.RequestEntity;
import org.apache.http.client.fluent.Request;

public class RequestInputParam extends ExecutionInputParam<RequestEntity> {
    private Request request;

    public RequestInputParam() {
    }

    public RequestInputParam(RequestEntity entity, Request request) {
        super(entity);
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}

package com.davidlong.creeper.model;

import com.davidlong.creeper.model.seq.RequestEntity;
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

package com.dlong.creeper.model.seq;

import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.handler.entity.AfterHandler;
import com.dlong.creeper.execution.handler.entity.BeforeHandler;
import com.dlong.creeper.execution.handler.entity.ExecutionHandler;
import com.dlong.creeper.execution.handler.info.JsonResultCookieInfo;
import com.dlong.creeper.execution.request.DefaultRequestBuilder;
import com.dlong.creeper.model.log.RequestLogInfo;
import com.dlong.creeper.model.log.ResponseLogInfo;
import org.apache.http.client.fluent.Request;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class RequestEntity extends LoopableEntity{
    private RequestInfo requestInfo;
    private RequestLogInfo requestLogInfo;
    private ResponseLogInfo responseLogInfo;
    private List<JsonResultCookieInfo> jsonResultCookieInfoList;
    private Request request;
    protected AnnotatedElement entityElement;

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public List<JsonResultCookieInfo> getJsonResultCookieInfoList() {
        return jsonResultCookieInfoList;
    }

    public void setJsonResultCookieInfoList(List<JsonResultCookieInfo> jsonResultCookieInfoList) {
        this.jsonResultCookieInfoList = jsonResultCookieInfoList;
    }

    public RequestLogInfo getRequestLogInfo() {
        return requestLogInfo;
    }

    public void setRequestLogInfo(RequestLogInfo requestLogInfo) {
        this.requestLogInfo = requestLogInfo;
    }

    public ResponseLogInfo getResponseLogInfo() {
        return responseLogInfo;
    }

    public void setResponseLogInfo(ResponseLogInfo responseLogInfo) {
        this.responseLogInfo = responseLogInfo;
    }

    public AnnotatedElement getEntityElement() {
        return entityElement;
    }

    public void setEntityElement(AnnotatedElement entityElement) {
        this.entityElement = entityElement;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Request buildRequest(ExecutionContext context) {
        return this.request = new DefaultRequestBuilder(context).buildRequest(this.requestInfo);
    }

    public void setExecutionHandler(ExecutionHandler handler) {
        super.setExecutionHandler(handler);
    }

    public void setAfterHandler(AfterHandler handler) {
        super.setAfterHandler(handler);
    }

    public void setBeforeHandler(BeforeHandler handler) {
        super.setBeforeHandler(handler);
    }
}

package com.dlong.creeper.model.result;

import org.apache.http.HttpResponse;

public class HttpResult{
    private HttpResponse httpResponse;
    private String content;

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

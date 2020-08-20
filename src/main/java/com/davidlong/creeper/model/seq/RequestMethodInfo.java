package com.davidlong.creeper.model.seq;
public class RequestMethodInfo {
    private String httpMethod;
    private String url;

    public RequestMethodInfo(String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    public RequestMethodInfo() {
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package com.davidlong.creeper.model.seq;

import com.davidlong.creeper.model.Param;
import org.apache.http.Header;

import java.util.LinkedList;
import java.util.List;

public class RequestInfo {
    private String httpMethod;
    private String url;
    private List<Header> headers;
    private List<Param> params;

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

    public List<Param> getParams() {
        return params==null?new LinkedList<>():params;
    }

    public List<Header> getHeaders() {
        return headers==null?new LinkedList<>():headers;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        if(httpMethod!=null){
            return "< "+httpMethod.toUpperCase()+" "+url+" >";
        }
        return "< "+url+" >";
    }
}

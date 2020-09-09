package com.dlong.creeper.model.seq;

import com.dlong.creeper.model.Param;
import org.apache.http.Header;
import org.apache.http.HttpHost;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RequestInfo {
    private String httpMethod;
    private String url;
    private List<Header> headers;
    private List<Param> params;
    private ProxyInfo proxyInfo;

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

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public void setProxyInfo(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    @Override
    public String toString() {
        if(httpMethod!=null){
            return "< "+httpMethod.toUpperCase()+" "+url+" >";
        }
        return "< "+url+" >";
    }
}

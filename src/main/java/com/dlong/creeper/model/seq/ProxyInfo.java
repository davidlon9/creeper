package com.dlong.creeper.model.seq;

import org.apache.http.HttpHost;

public class ProxyInfo {
    private HttpHost proxy;
    private String proxysContextKey;

    public ProxyInfo(HttpHost proxy) {
        this.proxy = proxy;
    }

    public ProxyInfo(String proxysContextKey) {
        this.proxysContextKey = proxysContextKey;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    public String getProxysContextKey() {
        return proxysContextKey;
    }

    public void setProxysContextKey(String proxysContextKey) {
        this.proxysContextKey = proxysContextKey;
    }
}

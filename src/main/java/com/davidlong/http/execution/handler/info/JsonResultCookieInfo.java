package com.davidlong.http.execution.handler.info;

public class JsonResultCookieInfo {
    /**
     * 返回的JsonResponse中,要作为cookie的key，多级用. 例如：result.token
     * @return
     */
    private String jsonKey;

    /**
     * Cookie的属性
     */
    private String name;
    private String defaultValue;
    private String domain;
    private String path;
    private long expiry;

    private boolean cache;

    public String getJsonKey() {
        return jsonKey;
    }

    public void setJsonKey(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }
}

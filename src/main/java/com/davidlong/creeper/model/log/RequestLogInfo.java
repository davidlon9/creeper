package com.davidlong.http.model.log;

public class RequestLogInfo {
    private boolean showUrl = true;
    private boolean showFilledParams = true;
    private boolean showFilledHeaders = true;

    public boolean isShowUrl() {
        return showUrl;
    }

    public void setShowUrl(boolean showUrl) {
        this.showUrl = showUrl;
    }

    public boolean isShowFilledParams() {
        return showFilledParams;
    }

    public void setShowFilledParams(boolean showFilledParams) {
        this.showFilledParams = showFilledParams;
    }

    public boolean isShowFilledHeaders() {
        return showFilledHeaders;
    }

    public void setShowFilledHeaders(boolean showFilledHeaders) {
        this.showFilledHeaders = showFilledHeaders;
    }
}

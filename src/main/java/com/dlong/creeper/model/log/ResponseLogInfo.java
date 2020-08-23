package com.dlong.creeper.model.log;

public class ResponseLogInfo {
    private boolean showResult = false;
    private boolean showStatus = true;
    private boolean showSetCookies = false;

    public boolean isShowResult() {
        return showResult;
    }

    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }

    public boolean isShowStatus() {
        return showStatus;
    }

    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }

    public boolean isShowSetCookies() {
        return showSetCookies;
    }

    public void setShowSetCookies(boolean showSetCookies) {
        this.showSetCookies = showSetCookies;
    }
}

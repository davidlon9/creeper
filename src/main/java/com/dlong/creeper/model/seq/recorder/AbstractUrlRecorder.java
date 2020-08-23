package com.dlong.creeper.model.seq.recorder;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUrlRecorder implements UrlRecorder {
    private Set<String> urlRecords = new HashSet<>(0);

    public Set<String> getUrlRecords() {
        return urlRecords;
    }

    public void addUrlRecord(String historicalUrl) {
        this.urlRecords.add(historicalUrl);
    }

    public boolean isUrlRecorded(String historicalUrl){
        return this.urlRecords.size()>0 && this.urlRecords.contains(historicalUrl);
    }
}

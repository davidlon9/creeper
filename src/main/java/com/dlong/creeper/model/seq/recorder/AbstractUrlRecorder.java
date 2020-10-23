package com.dlong.creeper.model.seq.recorder;

import com.dlong.creeper.execution.context.ChainContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUrlRecorder implements UrlRecorder {
    private boolean isReaded = false;
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

    @Override
    public Set<String> readUrlRecords(ChainContext context) throws IOException {
        Set<String> urls = doReadRecords(context);
        this.isReaded = true;
        return urls;
    }

    protected abstract Set<String> doReadRecords(ChainContext context) throws IOException;

    @Override
    public boolean isReaded() {
        return isReaded;
    }
}

package com.dlong.creeper.model.seq.recorder;

import com.dlong.creeper.execution.context.ChainContext;

import java.io.IOException;
import java.util.Set;

public interface UrlRecorder {
    Set<String> getUrlRecords();

    void addUrlRecord(String url);

    void writeUrlRecords(ChainContext context) throws IOException;

    Set<String> readUrlRecords(ChainContext context) throws IOException;

    boolean isUrlRecorded(String url);

    boolean isReaded();
}

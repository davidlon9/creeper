package com.dlong.creeper.model.seq.recorder;

import java.io.IOException;
import java.util.Set;

public interface UrlRecorder {
    Set<String> getUrlRecords();

    void addUrlRecord(String url);

    void writeUrlRecords() throws IOException;

    Set<String> readUrlRecords() throws IOException;

    boolean isUrlRecorded(String url);
}

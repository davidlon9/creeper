package com.dlong.creeper.model.seq.recorder;

import com.dlong.creeper.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class FileUrlRecorder extends AbstractUrlRecorder {
    private File recordFile;

    private int perIterateTimesUpdate = 10;

    private int currentIterateCount = 0;

    public FileUrlRecorder(File recordFile) {
        this.recordFile = recordFile;
    }

    public int getPerIterateTimesUpdate() {
        return perIterateTimesUpdate;
    }

    public void setPerIterateTimesUpdate(int perIterateTimesUpdate) {
        this.perIterateTimesUpdate = perIterateTimesUpdate;
    }

    public File getRecordFile() {
        return recordFile;
    }

    public void setRecordFile(File recordFile) {
        this.recordFile = recordFile;
    }

    public int getCurrentIterateCount() {
        return currentIterateCount;
    }

    public void setCurrentIterateCount(int currentIterateCount) {
        this.currentIterateCount = currentIterateCount;
    }

    public boolean isIterateToCount(){
        return this.currentIterateCount%this.perIterateTimesUpdate == 0 && this.currentIterateCount>=this.perIterateTimesUpdate ;
    }

    @Override
    public void addUrlRecord(String historicalUrl) {
        super.addUrlRecord(historicalUrl);
        this.currentIterateCount++;
    }

    public void writeUrlRecords() throws IOException {
        Set<String> historicalUrls = super.getUrlRecords();
        StringBuilder sb=new StringBuilder();
        if (historicalUrls.size()>0) {
            for (String historicalUrl : historicalUrls) {
                sb.append(historicalUrl).append("\n");
            }
        }
        FileUtil.write(this.recordFile,sb.toString());
    }

    public Set<String> readUrlRecords() throws IOException {
        currentIterateCount = 0;
        BufferedReader reader = new BufferedReader(new FileReader(this.recordFile));
        super.getUrlRecords().clear();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("http")) {
                addUrlRecord(line);
            }
        }
        return getUrlRecords();
    }
}

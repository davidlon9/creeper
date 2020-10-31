package com.dlong.creeper.model.seq.recorder;

import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class ExcelDataRecorder extends BaseRecorder {
    private File excelFile;
    private String urlColName;
    private String dataListContextKey;
    private Integer excelDataSize;

    public ExcelDataRecorder(File excelFile) {
        this.excelFile = excelFile;
    }

    public File getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(File excelFile) {
        this.excelFile = excelFile;
    }

    public String getUrlColName() {
        return urlColName;
    }

    public void setUrlColName(String urlColName) {
        this.urlColName = urlColName;
    }

    public String getDataListContextKey() {
        return dataListContextKey;
    }

    public void setDataListContextKey(String dataListContextKey) {
        this.dataListContextKey = dataListContextKey;
    }

    public Integer getExcelDataSize() {
        return excelDataSize;
    }

    public void setExcelDataSize(Integer excelDataSize) {
        this.excelDataSize = excelDataSize;
    }

    @Override
    public void addUrlRecord(String historicalUrl) {
        super.addUrlRecord(historicalUrl);
    }

    public void writeUrlRecords(ChainContext context) throws IOException {
        Set<String> historicalUrls = super.getUrlRecords();
        StringBuilder sb=new StringBuilder();
        if (historicalUrls.size()>0) {
            for (String historicalUrl : historicalUrls) {
                sb.append(historicalUrl).append("\n");
            }
        }
        FileUtil.write(this.excelFile,sb.toString());
    }

    @Override
    protected Set<String> doReadRecords(ChainContext context) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.excelFile));
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

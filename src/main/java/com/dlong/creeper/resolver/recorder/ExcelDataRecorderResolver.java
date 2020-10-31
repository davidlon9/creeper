package com.dlong.creeper.resolver.recorder;

import com.dlong.creeper.annotation.control.recorder.RecordeDataToExcel;
import com.dlong.creeper.annotation.control.recorder.RecordeUrlToFile;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.recorder.ExcelDataRecorder;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import com.dlong.creeper.resolver.base.AnnotationResolver;
import com.dlong.creeper.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class ExcelDataRecorderResolver extends AnnotationResolver implements RecorderResolver{
    public static final String DEFAULT_RECORD_FILE_DIR_NAME = "records";
    public static final String DEFAULT_RECORD_FILE_NAME = "data.xlsx";

    public ExcelDataRecorderResolver(AnnotatedElement target) {
        super(target,RecordeUrlToFile.class);
    }

    @Override
    public UrlRecorder resolve() throws AnnotationNotFoundException {
        Annotation annotation = super.resolveAnnotation();
        RecordeDataToExcel recordeDataToExcel = (RecordeDataToExcel) annotation;
        if(recordeDataToExcel != null){
            String path = recordeDataToExcel.excelPath();
            File file;
            if(path.equals("/")){
                file = new File(System.getProperty("user.dir") + File.separator + DEFAULT_RECORD_FILE_DIR_NAME + File.separator + DEFAULT_RECORD_FILE_NAME);
            }else{
                file = new File(path);
            }
            if(file.isDirectory()){
                file = new File(path+File.separator+file.getName()+".xlsx");
            }
            try {
                FileUtil.createIfNotExists(file);
                ExcelDataRecorder dataRecorder = new ExcelDataRecorder(file);
                dataRecorder.setExcelFile(file);
                dataRecorder.setDataListContextKey(recordeDataToExcel.dataListContextKey());
                dataRecorder.setUrlColName(recordeDataToExcel.urlColName());
                dataRecorder.setWriteStrategy(recordeDataToExcel.writeStrategy());
                return dataRecorder;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}

package com.davidlong.creeper.resolver.recorder;

import com.davidlong.creeper.annotation.control.FileRecordsIgnore;
import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.recorder.FileUrlRecorder;
import com.davidlong.creeper.model.seq.recorder.UrlRecorder;
import com.davidlong.creeper.resolver.base.AnnotationResolver;
import com.davidlong.creeper.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class FileRecordsIgnoreResolver extends AnnotationResolver implements RecorderResolver{
    public static final String DEFAULT_RECORD_FILE_DIR_NAME = "records";
    public static final String DEFAULT_RECORD_FILE_NAME = "all.txt";

    public FileRecordsIgnoreResolver(AnnotatedElement target) {
        super(target,FileRecordsIgnore.class);
    }

    @Override
    public UrlRecorder resolve() throws AnnotationNotFoundException {
        Annotation annotation = super.resolveAnnotation();
        FileRecordsIgnore fileRecordsIgnore = (FileRecordsIgnore) annotation;
        if(fileRecordsIgnore != null){
            String path = fileRecordsIgnore.filePath();
            File file;
            if(path.equals("/")){
                file = new File(System.getProperty("user.dir") + File.separator + DEFAULT_RECORD_FILE_DIR_NAME + File.separator + DEFAULT_RECORD_FILE_NAME);
            }else{
                file = new File(path);
            }
            if(file.isDirectory()){
                file = new File(path+File.separator+file.getName()+".txt");
            }
            try {
                FileUtil.createIfNotExists(file);
                FileUrlRecorder fileUrlRecorder = new FileUrlRecorder(file);
                fileUrlRecorder.readUrlRecords();
                fileUrlRecorder.setRecordFile(file);
                fileUrlRecorder.setPerIterateTimesUpdate(fileRecordsIgnore.perIterateTimesUpdate());
                return fileUrlRecorder;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}

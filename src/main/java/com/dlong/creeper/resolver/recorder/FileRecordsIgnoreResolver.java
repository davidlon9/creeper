package com.dlong.creeper.resolver.recorder;

import com.dlong.creeper.annotation.control.recorder.FileUrlRecorder;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import com.dlong.creeper.resolver.base.AnnotationResolver;
import com.dlong.creeper.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class FileRecordsIgnoreResolver extends AnnotationResolver implements RecorderResolver{
    public static final String DEFAULT_RECORD_FILE_DIR_NAME = "records";
    public static final String DEFAULT_RECORD_FILE_NAME = "all.txt";

    public FileRecordsIgnoreResolver(AnnotatedElement target) {
        super(target,FileUrlRecorder.class);
    }

    @Override
    public UrlRecorder resolve() throws AnnotationNotFoundException {
        Annotation annotation = super.resolveAnnotation();
        FileUrlRecorder fileRecorder = (FileUrlRecorder) annotation;
        if(fileRecorder != null){
            String path = fileRecorder.filePath();
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
                com.dlong.creeper.model.seq.recorder.FileUrlRecorder fileUrlRecorder = new com.dlong.creeper.model.seq.recorder.FileUrlRecorder(file);
                fileUrlRecorder.setRecordFile(file);
                fileUrlRecorder.setPerIterateTimesUpdate(fileRecorder.perIterateTimesUpdate());
                return fileUrlRecorder;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}

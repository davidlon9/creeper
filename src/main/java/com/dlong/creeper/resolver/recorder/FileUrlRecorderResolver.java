package com.dlong.creeper.resolver.recorder;

import com.dlong.creeper.annotation.control.recorder.RecordeUrlToFile;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.recorder.FileUrlRecorder;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import com.dlong.creeper.resolver.base.AnnotationResolver;
import com.dlong.creeper.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class FileUrlRecorderResolver extends AnnotationResolver implements RecorderResolver{
    public static final String DEFAULT_RECORD_FILE_DIR_NAME = "records";
    public static final String DEFAULT_RECORD_FILE_NAME = "all.txt";

    public FileUrlRecorderResolver(AnnotatedElement target) {
        super(target,RecordeUrlToFile.class);
    }

    @Override
    public UrlRecorder resolve() throws AnnotationNotFoundException {
        Annotation annotation = super.resolveAnnotation();
        RecordeUrlToFile fileRecorder = (RecordeUrlToFile) annotation;
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
                FileUrlRecorder fileUrlRecorder = new FileUrlRecorder(file);
                fileUrlRecorder.setRecordFile(file);
                fileUrlRecorder.setPerIterateTimesUpdate(fileRecorder.perIterateTimesUpdate());
                fileUrlRecorder.setWriteStrategy(fileRecorder.writeStrategy());
                return fileUrlRecorder;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}

package com.dlong.creeper.resolver.recorder;

import com.dlong.creeper.annotation.control.DataSource;
import com.dlong.creeper.annotation.control.recorder.DatabaseRecordsIgnore;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.recorder.DatabaseUrlRecorder;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;
import com.dlong.creeper.resolver.base.AnnotationResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class DatabaseRecordsIgnoreResolver extends AnnotationResolver implements RecorderResolver{
    public DatabaseRecordsIgnoreResolver(AnnotatedElement target) {
        super(target,DatabaseRecordsIgnore.class);
    }

    @Override
    public UrlRecorder resolve() throws AnnotationNotFoundException {
        Annotation annotation = super.resolveAnnotation();
        DatabaseRecordsIgnore dri = (DatabaseRecordsIgnore) annotation;
        if(dri != null){
            DataSource dataSource = dri.dataSource();
            return new DatabaseUrlRecorder(
                    new DatabaseUrlRecorder.DataSource(dataSource.driverClass(),dataSource.url(),dataSource.username(),dataSource.password()),
                    dri.tableName(),
                    dri.urlColName()
            );
        }
        return null;
    }
}

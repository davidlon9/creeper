package com.davidlong.http.resolver.recorder;

import com.davidlong.http.annotation.control.DataSource;
import com.davidlong.http.annotation.control.DatabaseRecordsIgnore;
import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.recorder.DatabaseUrlRecorder;
import com.davidlong.http.model.seq.recorder.UrlRecorder;
import com.davidlong.http.resolver.base.AnnotationResolver;

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

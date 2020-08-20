package com.davidlong.http.resolver.recorder;

import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.recorder.UrlRecorder;

public interface RecorderResolver {
    UrlRecorder resolve() throws AnnotationNotFoundException;
}

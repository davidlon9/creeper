package com.davidlong.creeper.resolver.recorder;

import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.recorder.UrlRecorder;

public interface RecorderResolver {
    UrlRecorder resolve() throws AnnotationNotFoundException;
}

package com.dlong.creeper.resolver.recorder;

import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;

public interface RecorderResolver {
    UrlRecorder resolve() throws AnnotationNotFoundException;
}

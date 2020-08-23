package com.dlong.creeper.resolver.looper;

import com.dlong.creeper.annotation.control.looper.ForIndex;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.control.ForIndexLooper;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public class ForIndexLooperResolver extends BaseLooperResolver<ForIndexLooper>{
    public ForIndexLooperResolver(AnnotatedElement target) {
        super(target,ForIndex.class);
    }

    @Override
    public ForIndexLooper resolve() throws AnnotationNotFoundException {
        ForIndexLooper forIndexLooper = super.resolve(new ForIndexLooper());

        Map<String,Object> attributes = AnnotationUtils.getAnnotationAttributes(getAnnotation());
        Object start = attributes.get("start");
        Object end = attributes.get("end");
        Object indexName = attributes.get("indexName");

        forIndexLooper.setStart(start.toString());
        forIndexLooper.setEnd(end.toString());
        forIndexLooper.setIndexName(indexName.toString());
        return forIndexLooper;
    }
}

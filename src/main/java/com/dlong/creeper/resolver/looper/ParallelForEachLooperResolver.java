package com.dlong.creeper.resolver.looper;

import com.dlong.creeper.annotation.control.looper.ParallelForEach;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.control.ParallelForEachLooper;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public class ParallelForEachLooperResolver extends BaseLooperResolver<ParallelForEachLooper>{

    public ParallelForEachLooperResolver(AnnotatedElement target) {
        super(target,ParallelForEach.class);
    }

    @Override
    public ParallelForEachLooper resolve() throws AnnotationNotFoundException {
        ParallelForEachLooper forEachLooper = super.resolve(new ParallelForEachLooper());
        Map<String,Object> attributes = AnnotationUtils.getAnnotationAttributes(getAnnotation());
        Object itemsContextKey = attributes.get("itemsContextKey");
        Object itemName = attributes.get("itemName");

        forEachLooper.setItemName(itemName.toString());
        forEachLooper.setItemsContextKey(itemsContextKey.toString());
        return forEachLooper;
    }
}

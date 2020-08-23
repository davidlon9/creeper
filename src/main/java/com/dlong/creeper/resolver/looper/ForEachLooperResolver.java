package com.dlong.creeper.resolver.looper;

import com.dlong.creeper.annotation.control.looper.ForEach;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.control.ForEachLooper;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public class ForEachLooperResolver extends BaseLooperResolver<ForEachLooper>{

    public ForEachLooperResolver(AnnotatedElement target) {
        super(target,ForEach.class);
    }

    @Override
    public ForEachLooper resolve() throws AnnotationNotFoundException {
        ForEachLooper forEachLooper = super.resolve(new ForEachLooper());
        Map<String,Object> attributes = AnnotationUtils.getAnnotationAttributes(getAnnotation());
        Object itemsContextKey = attributes.get("itemsContextKey");
        Object itemName = attributes.get("itemName");

        forEachLooper.setItemName(itemName.toString());
        forEachLooper.setItemsContextKey(itemsContextKey.toString());
        return forEachLooper;
    }
}

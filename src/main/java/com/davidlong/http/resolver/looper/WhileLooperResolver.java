package com.davidlong.http.resolver.looper;

import com.davidlong.http.annotation.control.looper.While;
import com.davidlong.http.exception.AnnotationNotFoundException;
import com.davidlong.http.model.seq.control.WhileLooper;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public class WhileLooperResolver extends BaseLooperResolver<WhileLooper>{

    public WhileLooperResolver(AnnotatedElement target) {
        super(target,While.class);
    }

    @Override
    public WhileLooper resolve() throws AnnotationNotFoundException {
        WhileLooper whileLooper = super.resolve(new WhileLooper());
        Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(getAnnotation());
        Object coniditionExpression = attributes.get("coniditionExpression");
        whileLooper.setConiditionExpression(coniditionExpression.toString());
        return whileLooper;
    }
}

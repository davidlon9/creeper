package com.davidlong.creeper.resolver;

import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class GenericAnnotationResolver {
    private static Logger logger=Logger.getLogger(GenericAnnotationResolver.class);

    public static <T> T resolve(Annotation annotation,Class<T> clz){
        Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
        try {
            T t = clz.newInstance();
            Set<Map.Entry<String, Object>> entries = attributes.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String attrName = entry.getKey();
                Field field;
                try {
                    field = clz.getDeclaredField(attrName);
                } catch (NoSuchFieldException e) {
                    logger.warn("class does't contain "+annotation+" annotation attribute name corresponding field");
                    continue;
                }
                if(field!=null){
                    field.setAccessible(true);
                    field.set(t,entry.getValue());
                }
            }
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

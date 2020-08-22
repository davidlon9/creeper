package com.davidlong.creeper.resolver.util;

import com.davidlong.creeper.annotation.http.Get;
import com.davidlong.creeper.annotation.http.Path;
import com.davidlong.creeper.annotation.seq.Chain;
import com.davidlong.creeper.annotation.seq.Request;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ResolveUtil {
    public static boolean isRequestMethod(Method method) {
        Annotation[] annotations = AnnotationUtils.getAnnotations(method);
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().isAnnotationPresent(Request.class)){
                return true;
            }
        }
        return false;
    }

    public static boolean isRequestField(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().isAnnotationPresent(Request.class)){
                return true;
            }
        }
        return false;
    }

    public static boolean isAnnotatedPath(AnnotatedElement element) {
        Annotation[] annotations = element.getAnnotations();
        for (Annotation annotation : annotations) {
            if(isPathAnno(annotation)){
                return true;
            }
        }
        return false;
    }

    public static boolean isPathAnno(Annotation annotation) {
        return annotation.annotationType().isAnnotationPresent(Path.class) || annotation.annotationType().equals(Path.class);
    }

    public static boolean isChainClass(Class<?> clz) {
        Annotation[] annotations = clz.getAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation.annotationType().isAnnotationPresent(Chain.class)){
                return true;
            }
        }
        return false;
    }

}

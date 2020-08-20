package com.davidlong.creeper.resolver.base;

import com.davidlong.creeper.HttpConst;
import com.davidlong.creeper.annotation.Host;
import com.davidlong.creeper.annotation.Path;
import com.davidlong.creeper.annotation.http.*;
import com.davidlong.creeper.model.seq.RequestInfo;
import com.davidlong.creeper.resolver.RequestUrlInfoResolver;
import com.davidlong.creeper.resolver.util.ResolveUtil;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultRequestUrlInfoResolver implements RequestUrlInfoResolver {
    private Logger logger=Logger.getLogger(DefaultRequestUrlInfoResolver.class);

    public static final List<Class> ACTION_ANNOS = Arrays.asList(new Class[]{Action.class, Get.class, Post.class, Delete.class, Put.class});

    public Map<AnnotatedElement,RequestInfo> resolve(Class chainClass){
        Map<AnnotatedElement,RequestInfo> requestInfoMap = new HashMap<>();
        if(requestInfoMap.size()>0){
            requestInfoMap.clear();
        }
        resolve("", chainClass,requestInfoMap);
        return requestInfoMap;
    }

    public Map<AnnotatedElement,RequestInfo> resolve(String basePath,Class resolveClass,Map<AnnotatedElement,RequestInfo> requestInfoMap){
        basePath = "".equals(basePath)?getPath("", resolveClass):basePath;

        requestInfoMap.putAll(resolveRequestTarget(basePath, resolveClass));

        Class<?>[] classes = resolveClass.getDeclaredClasses();
        if(classes.length>0){
            for (Class<?> clz : classes) {
                resolve(getPath(basePath, clz), clz, requestInfoMap);
            }
        }
        return requestInfoMap;
    }

    private Map<AnnotatedElement,RequestInfo> resolveRequestTarget(String basePath, Class clz){
        Map<AnnotatedElement,RequestInfo> requestInfoMap = new HashMap<>();
        ReflectionUtils.doWithMethods(clz, method -> putRequestInfo(method,basePath,requestInfoMap), ResolveUtil::isRequestMethod);
        ReflectionUtils.doWithFields(clz, field -> putRequestInfo(field,basePath,requestInfoMap), ResolveUtil::isRequestField);
        return requestInfoMap;
    }

    private void putRequestInfo(AnnotatedElement target,String basePath, Map<AnnotatedElement, RequestInfo> requestInfoMap) {
        RequestInfo requestInfo = new RequestInfo();
        String url = getPath(basePath, target);
        requestInfo.setUrl(url);
        requestInfo.setHttpMethod(getHttpMethod(target));
        Member member = (Member) target;
        logger.info("resolved prepare url "+requestInfo+" for target ["+ member.getName()+"] in ["+member.getDeclaringClass().getSimpleName()+"]");
        requestInfoMap.put(target, requestInfo);
    }

    private String getPath(String base, Object obj){
        if(obj instanceof Class){
            base = buildBase(base,obj);
        }else if(obj instanceof AnnotatedElement){
            StringBuilder sb=new StringBuilder(buildBase(base, obj));
            Annotation[] annotations = ((AnnotatedElement)obj).getAnnotations();
            for (Annotation annotation : annotations) {
                if(isActionAnno(annotation)){
                    String url = (String) AnnotationUtils.getAnnotationAttributes(annotation).get("value");
                    Boolean urlInheritable = (Boolean) AnnotationUtils.getAnnotationAttributes(annotation).get("urlInheritable");
                    if(!urlInheritable){
                        return url;
                    }
                    if(!url.startsWith("/")){
                        sb.append("/");
                    }
                    sb.append(url);
                    break;
                }
            }
            base=sb.toString();
        }
        return base;
    }

    private String buildBase(String base,Object obj) {
        Host host =null;
        Path path =null;
        if(obj instanceof Class){
            Class clz = (Class) obj;
            host = AnnotationUtils.findAnnotation(clz, Host.class);
            path = AnnotationUtils.findAnnotation(clz, Path.class);
        }else if(obj instanceof AnnotatedElement){
            host = AnnotationUtils.getAnnotation((AnnotatedElement) obj, Host.class);
            path = AnnotationUtils.getAnnotation((AnnotatedElement) obj, Path.class);
        }

        if(host!=null){
            base=host.scheme()+HttpConst.URI_BEGIN_IDENTIFIER+host.value();
        }
        if(path!=null){
            String p = path.value();
            if(!p.startsWith("/")){
                base+="/";
            }
            base += p;
        }
        return base;
    }

    private String getHttpMethod(AnnotatedElement element) {
        Annotation[] annotations = element.getAnnotations();
        for (Annotation annotation : annotations) {
            if(isActionAnno(annotation)){
                if (annotation.annotationType().equals(Action.class)) {
                    return (String) AnnotationUtils.getAnnotationAttributes(annotation).get("method");
                }
                return annotation.annotationType().getSimpleName().toLowerCase();
            }
        }
        return null;
    }

    private boolean isActionAnno(Annotation annotation) {
        return ACTION_ANNOS.contains(annotation.annotationType());
    }
}

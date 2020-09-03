package com.dlong.creeper.resolver.base;

import com.dlong.creeper.annotation.result.JsonResultCookie;
import com.dlong.creeper.annotation.result.JsonResultCookies;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.execution.handler.info.JsonResultCookieInfo;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.RequestInfo;
import com.dlong.creeper.model.seq.SequentialEntity;
import com.dlong.creeper.resolver.util.WrapUtil;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BaseRequestAnnoResolver extends LoopableSeqResolver{
    private Logger logger = Logger.getLogger(BaseRequestAnnoResolver.class);

    public BaseRequestAnnoResolver(Class<?> handleClass, AnnotatedElement handler, Class<? extends Annotation> requestAnnoClass) {
        super(handleClass,handler,requestAnnoClass);
    }

    public RequestEntity resolveRequest(RequestInfo requestInfo,RequestEntity request) throws AnnotationNotFoundException {
        super.resolveLoopable(request);
        logger.info("Start resolving Request "+ WrapUtil.enAngleBrackets(request.getName()));

        request.setRequestInfo(requestInfo);

        List<JsonResultCookieInfo> jsonResultCookieInfoList = getJsonResultCookieInfoList();
        for (JsonResultCookieInfo info : jsonResultCookieInfoList) {
            logger.info("use json key "+WrapUtil.enDoubleQuote(info.getJsonKey())+" in json result as cookie for Request "+WrapUtil.enAngleBrackets(request.getName()));
        }
        request.setJsonResultCookieInfoList(jsonResultCookieInfoList);

        request.setEntityElement(getTarget());
        return request;
    }

    protected List<JsonResultCookieInfo> getJsonResultCookieInfoList() {
        JsonResultCookie[] jsonResultCookies = findJsonResultCookies();
        List<JsonResultCookieInfo> list=new ArrayList<>(jsonResultCookies.length);
        for (JsonResultCookie jsonResultCookie : jsonResultCookies) {
            JsonResultCookieInfo info=new JsonResultCookieInfo();
            info.setName(jsonResultCookie.name());
            info.setJsonKey(jsonResultCookie.jsonKey());
            info.setDefaultValue(jsonResultCookie.defaultValue());
            info.setCache(jsonResultCookie.cache());
            info.setExpiry(jsonResultCookie.expiry());
            info.setDomain(jsonResultCookie.domain());
            info.setPath(jsonResultCookie.path());
            list.add(info);
        }
        return list;
    }

    public JsonResultCookie[] findJsonResultCookies(){
        AnnotatedElement target = getTarget();
        JsonResultCookies jsonResultCookies = AnnotationUtils.getAnnotation(target, JsonResultCookies.class);
        JsonResultCookie jsonResultCookie = AnnotationUtils.getAnnotation(target, JsonResultCookie.class);

        JsonResultCookie[] resultCookies;
        if(jsonResultCookies!=null){
            resultCookies = jsonResultCookies.value();
        }else if(jsonResultCookie!=null){
            resultCookies=new JsonResultCookie[]{jsonResultCookie};
        }else{
            resultCookies=new JsonResultCookie[0];
        }
        return resultCookies;
    }

    @Override
    public String getDefaultName() {
        AnnotatedElement target = getTarget();
        if (target instanceof Method){
            return ((Method) target).getName();
        }else if(target instanceof Field){
            return ((Field) target).getName();
        }
        return super.getDefaultName();
    }

//    protected FailedStrategy getFailedStrategy(Method method) {
//        Class<?> returnType = method.getReturnType();
//        if(returnType.equals(Boolean.class) || returnType.getSimpleName().equals("boolean")){
//            Annotation[] annotations = AnnotationUtils.getAnnotations(method);
//            for (Annotation annotation : annotations) {
//                if(HttpConst.isFailedStrategyAnno(annotation)){
//                    Object jsonKey = AnnotationUtils.getAnnotationAttributes(annotation).get("jsonKey");
//                    if(annotation.annotationType().equals(FailedJump.class)){
//                        return FailedStrategy.JUMP((Integer) jsonKey);
//                    }else if(annotation.annotationType().equals(FailedRetry.class)){
//                        FailedStrategy failedStrategy = HttpConst.FAILED_ANNO_MAPPING.get(FailedRetry.class);
//                        failedStrategy.setWait((Integer) jsonKey);
//                        return failedStrategy;
//                    }
//                    return HttpConst.FAILED_ANNO_MAPPING.get(annotation.annotationType());
//                }
//            }
//        }else if(returnType.equals(FailedStrategy.class)){
//            return FailedStrategy.MUTABLE;
//        }
//        return FailedStrategy.FORWARD;
//    }

    public static void main(String[] args) {
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setIndex(1);
        requestEntity.setDescription("abc");
        requestEntity.setName("abcsdasd");
        SequentialEntity sequentialEntity = requestEntity;
        System.out.println(sequentialEntity.toString());
    }
}

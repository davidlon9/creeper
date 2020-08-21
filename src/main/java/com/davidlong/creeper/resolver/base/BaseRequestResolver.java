package com.davidlong.creeper.resolver.base;

import com.davidlong.creeper.annotation.Parameter;
import com.davidlong.creeper.annotation.Parameters;
import com.davidlong.creeper.annotation.RequestHeader;
import com.davidlong.creeper.annotation.RequestHeaders;
import com.davidlong.creeper.annotation.result.JsonResultCookie;
import com.davidlong.creeper.annotation.result.JsonResultCookies;
import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.execution.handler.info.JsonResultCookieInfo;
import com.davidlong.creeper.model.Param;
import com.davidlong.creeper.model.seq.RequestEntity;
import com.davidlong.creeper.model.seq.RequestInfo;
import com.davidlong.creeper.model.seq.SequentialEntity;
import com.davidlong.creeper.resolver.util.WrapUtil;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BaseRequestResolver extends LoopableSeqResolver{
    private Logger logger = Logger.getLogger(BaseRequestResolver.class);

    public BaseRequestResolver(Class<?> handleClass, AnnotatedElement handler, Class<? extends Annotation> requestAnnoClass) {
        super(handleClass,handler,requestAnnoClass);
    }

    public RequestEntity resolveRequest(RequestInfo requestInfo,RequestEntity request) throws AnnotationNotFoundException {
        super.resolveLoopable(request);
        logger.info("Start resolving Request "+ WrapUtil.enAngleBrackets(request.getName()));

        List<Param> requestParams = getRequestParams();
        requestInfo.setParams(requestParams);
        for (Param param : requestParams) {
            logger.info("add prepare Param "+param+" to Request "+ WrapUtil.enAngleBrackets(request.getName()));
        }

        List<Header> requestHeaders = getRequestHeaders();
        requestInfo.setHeaders(requestHeaders);
        for (Header header : requestHeaders) {
            logger.info("add Header "+WrapUtil.enAngleBrackets(header.toString(),true)+" to Request "+ WrapUtil.enAngleBrackets(request.getName()));
        }
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

    protected List<Param> getRequestParams() {
        List<Param> params=new ArrayList<>();
        AnnotatedElement target = getTarget();
        Parameters parametersAnno = AnnotationUtils.getAnnotation(target, Parameters.class);
        Parameter parameterAnno = AnnotationUtils.getAnnotation(target, Parameter.class);
        if(parametersAnno!=null){
            Parameter[] parameters = parametersAnno.value();
            for (Parameter parameter : parameters) {
                Param param = new Param(parameter.name(), parameter.value());
                if(!"".equals(parameter.globalKey())){
                    param.setGlobalKey(parameter.globalKey());
                }
                params.add(param);
            }
        }else if(parameterAnno!=null){
            Param param = new Param(parameterAnno.name(), parameterAnno.value());
            if(!"".equals(parameterAnno.globalKey())){
                param.setGlobalKey(parameterAnno.globalKey());
            }
            params.add(param);
        }
        return params;
    }

    protected List<Header> getRequestHeaders() {
        List<Header> headers=new ArrayList<>();
        AnnotatedElement target = getTarget();
        RequestHeaders requestHeadersAnno = AnnotationUtils.getAnnotation(target, RequestHeaders.class);
        RequestHeader requestHeaderAnno = AnnotationUtils.getAnnotation(target, RequestHeader.class);
        if(requestHeadersAnno!=null){
            RequestHeader[] requestHeaders = requestHeadersAnno.value();
            for (RequestHeader requestHeader : requestHeaders) {
                headers.add(new BasicHeader(requestHeader.name(),requestHeader.value()));
            }
        }else if(requestHeaderAnno!=null){
            headers.add(new BasicHeader(requestHeaderAnno.name(),requestHeaderAnno.value()));
        }
        return headers;
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

package com.dlong.creeper.resolver;

import com.dlong.creeper.HttpConst;
import com.dlong.creeper.annotation.*;
import com.dlong.creeper.annotation.http.Path;
import com.dlong.creeper.exception.RuntimeResolveException;
import com.dlong.creeper.model.Param;
import com.dlong.creeper.model.seq.ProxyInfo;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.RequestInfo;
import com.dlong.creeper.resolver.util.ResolveUtil;
import com.dlong.creeper.resolver.util.WrapUtil;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultRequestInfoResolver implements RequestInfoResolver {
    private Logger logger= Logger.getLogger(DefaultRequestInfoResolver.class);

    public Map<AnnotatedElement,RequestInfo> resolve(Class chainClass){
        return resolve(chainClass,null);
    }

    public Map<AnnotatedElement,RequestInfo> resolve(Class chainClass, RequestChainEntity parentChain){
        Map<AnnotatedElement,RequestInfo> requestInfoMap = new HashMap<>();
        if(requestInfoMap.size()>0){
            requestInfoMap.clear();
        }
        String basePath = "";
        ProxyInfo baseProxy = null;
        if (parentChain!=null){
            basePath = getBasePath(parentChain);
            baseProxy = getBaseProxy(parentChain);
        }
        resolve(basePath, baseProxy, chainClass,requestInfoMap);
        return requestInfoMap;
    }

    private ProxyInfo getBaseProxy(RequestChainEntity parentChain) {
        ProxyInfo baseProxy = getProxyInfo(parentChain.getChainClass());
        if(baseProxy == null && parentChain.getParent()!=null){
            baseProxy = getBaseProxy(parentChain.getParent());
        }
        return baseProxy;
    }

    private String getBasePath(RequestChainEntity parentChain) {
        String basePath = getPath("", parentChain.getChainClass());
        if("".equals(basePath) && parentChain.getParent()!=null){
            basePath = getBasePath(parentChain.getParent());
        }
        return basePath;
    }

    public Map<AnnotatedElement,RequestInfo> resolve(String basePath, ProxyInfo baseProxyInfo, Class resolveClass, Map<AnnotatedElement,RequestInfo> requestInfoMap){
        basePath = "".equals(basePath) ? getPath("", resolveClass) : basePath;
        ProxyInfo proxy = getProxyInfo(resolveClass);
        if(proxy == null){
            proxy = baseProxyInfo;
        }
        requestInfoMap.putAll(resolveRequestTarget(basePath, proxy, resolveClass));

        Class<?>[] classes = resolveClass.getDeclaredClasses();
        if(classes.length>0){
            for (Class<?> clz : classes) {
                resolve(getPath(basePath, clz), proxy, clz, requestInfoMap);
            }
        }
        return requestInfoMap;
    }

    private Map<AnnotatedElement,RequestInfo> resolveRequestTarget(String basePath, ProxyInfo baseProxyInfo, Class clz){
        Map<AnnotatedElement,RequestInfo> requestInfoMap = new HashMap<>();
        ReflectionUtils.doWithMethods(clz, method -> putRequestInfo(method,basePath,baseProxyInfo,requestInfoMap)
                , method -> ResolveUtil.isAnnotatedPath(method) || ResolveUtil.isAnnotatedRequest(method));
        ReflectionUtils.doWithFields(clz, field -> putRequestInfo(field,basePath,baseProxyInfo,requestInfoMap)
                ,  method -> ResolveUtil.isAnnotatedPath(method) || ResolveUtil.isAnnotatedRequest(method));
        return requestInfoMap;
    }

    private void putRequestInfo(AnnotatedElement target, String basePath, ProxyInfo baseProxyInfo, Map<AnnotatedElement, RequestInfo> requestInfoMap) {
        Member member = (Member) target;

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUrl(getRequestUrl(target, basePath));
        requestInfo.setHttpMethod(getHttpMethod(target));
        logger.info("set prepare Url "+requestInfo+" for target ["+ member.getName()+"] in ["+member.getDeclaringClass().getSimpleName()+"]");
        requestInfo.setParams(getRequestParams(target));
        requestInfo.setHeaders(getRequestHeaders(target));
        requestInfo.setProxyInfo(baseProxyInfo);

        ProxyInfo proxyInfo = getProxyInfo(target);
        if(proxyInfo != null){
            requestInfo.setProxyInfo(proxyInfo);
        }
        logger.info("resolved request info for target ["+ member.getName()+"] in ["+member.getDeclaringClass().getSimpleName()+"]\n");
        requestInfoMap.put(target, requestInfo);
    }

    private ProxyInfo getProxyInfo(AnnotatedElement target) {
        Proxy proxy = AnnotationUtils.getAnnotation(target, Proxy.class);
        if(proxy != null){
            HttpHost httpHost = new HttpHost(proxy.value(), proxy.port(), proxy.scheme());
            return new ProxyInfo(httpHost);
        }
        Proxys proxys = AnnotationUtils.getAnnotation(target, Proxys.class);
        if(proxys != null){
            return new ProxyInfo(proxys.contextKey());
        }
        return null;
    }

    private String getRequestUrl(AnnotatedElement target, String basePath) {
        Member member = (Member) target;

        String url = getPath(basePath, target);
        if("".equals(basePath) && !(url.startsWith("http") || url.startsWith("${"))){
            if("".equals(url)){
                throw new RuntimeResolveException("resolved prepare Url is empty string, please annotate Path/Get/Post/Put/Delete annotation at request target "+ WrapUtil.enDoubleQuote(member.getName()));
            }else{
                throw new RuntimeResolveException("resolved prepare Url "+ WrapUtil.enDoubleQuote(url)+ " is invalid, url dose't have scheme, please make sure url start with http or class/method of sequential entity annotated with @Host");
            }
        }
        if(basePath.equals(url)){
            logger.warn("request target "+ WrapUtil.enBrackets(member.getName())+" does't have a path of host, request url resolved as host");
        }
        return url;
    }

    private String getPath(String base, Object obj){
        if(obj instanceof Class){
            base = buildBase(base,obj);
        }else if(obj instanceof AnnotatedElement){
            String basePath = buildBase(base, obj);
            StringBuilder sb=new StringBuilder(basePath);
            Annotation[] annotations = ((AnnotatedElement)obj).getAnnotations();
            for (Annotation annotation : annotations) {
                if(ResolveUtil.isPathAnno(annotation)){
                    String url = (String) AnnotationUtils.getAnnotationAttributes(annotation).get("value");
                    Boolean urlInheritable = (Boolean) AnnotationUtils.getAnnotationAttributes(annotation).get("urlInheritable");
                    if(!urlInheritable){
                        return url;
                    }
                    if(url.startsWith("http")){
                        sb.append(url);
                        return url;
                    }
                    if(!url.startsWith("/") && basePath.startsWith("http")){
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
            host = AnnotationUtils.getAnnotation(clz, Host.class);
            path = AnnotationUtils.getAnnotation(clz, Path.class);
        }else if(obj instanceof AnnotatedElement){
            AnnotatedElement el = (AnnotatedElement) obj;
            host = el.getAnnotation(Host.class);
            path = el.getAnnotation(Path.class);
        }

        if(host!=null){
            base=host.scheme()+ HttpConst.URI_BEGIN_IDENTIFIER+host.value();
        }

        if(path!=null){
            String p = path.value();
            if(p.startsWith("http")){
                return p;
            }else{
                if(!p.startsWith("/")){
                    base+="/";
                }
                base += p;
            }
        }
        return base;
    }

    private List<Param> getRequestParams(AnnotatedElement target) {
        List<Param> params=new ArrayList<>();
        Parameters parametersAnno = AnnotationUtils.getAnnotation(target, Parameters.class);
        Parameter parameterAnno = AnnotationUtils.getAnnotation(target, Parameter.class);
        if(parametersAnno!=null){
            Parameter[] parameters = parametersAnno.value();
            for (Parameter parameter : parameters) {
                addParamsToList(params, parameter, target);
            }
        }else if(parameterAnno!=null){
            addParamsToList(params, parameterAnno, target);
        }
        return params;
    }

    private void addParamsToList(List<Param> params, Parameter parameter, AnnotatedElement target) {
        Param param = new Param(parameter.name(), parameter.value());
        if(!"".equals(parameter.globalKey())){
            param.setGlobalKey(parameter.globalKey());
        }
        params.add(param);
        logger.info("add prepare Param "+param+" to Request target "+ WrapUtil.enAngleBrackets(((Member) target).getName()));
    }

    private List<Header> getRequestHeaders(AnnotatedElement target) {
        List<Header> headers=new ArrayList<>();
        RequestHeaders requestHeadersAnno = AnnotationUtils.getAnnotation(target, RequestHeaders.class);
        RequestHeader requestHeaderAnno = AnnotationUtils.getAnnotation(target, RequestHeader.class);
        if(requestHeadersAnno!=null){
            RequestHeader[] requestHeaders = requestHeadersAnno.value();
            for (RequestHeader requestHeader : requestHeaders) {
                addHeaderToList(headers, requestHeader, target);
            }
        }else if(requestHeaderAnno!=null){
            addHeaderToList(headers, requestHeaderAnno, target);
        }
        return headers;
    }

    private void addHeaderToList(List<Header> headers, RequestHeader requestHeader, AnnotatedElement target) {
        Header header = new BasicHeader(requestHeader.name(), requestHeader.value());
        headers.add(header);
        logger.info("add prepare Header "+ WrapUtil.enAngleBrackets(header.toString(),true)+" to Request "+ WrapUtil.enAngleBrackets(((Member) target).getName()));
    }

    private String getHttpMethod(AnnotatedElement element) {
        Annotation[] annotations = element.getAnnotations();
        for (Annotation annotation : annotations) {
            if(ResolveUtil.isPathAnno(annotation)){
                if (annotation.annotationType().equals(Path.class)) {
                    return (String) AnnotationUtils.getAnnotationAttributes(annotation).get("method");
                }
                return annotation.annotationType().getSimpleName().toLowerCase();
            }
        }
        return "get";
    }
}

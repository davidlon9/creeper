package com.dlong.creeper.execution.request;

import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;
import com.dlong.creeper.expression.ContextExpressionParser;
import com.dlong.creeper.model.Param;
import com.dlong.creeper.model.log.RequestLogInfo;
import com.dlong.creeper.model.seq.ProxyInfo;
import com.dlong.creeper.model.seq.RequestInfo;
import com.dlong.creeper.resolver.util.WrapUtil;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DefaultRequestBuilder implements HttpRequestBuilder {
    private ContextParamStore contextStore;
    private FormParamStore paramStore;
    private RequestLogInfo logInfo;

    public static final String templateIdentifier="${";

    private static Logger logger= Logger.getLogger(DefaultRequestBuilder.class);

    public DefaultRequestBuilder(ExecutionContext context) {
        this(context.getContextStore(),context.getParamStore());
    }

    public DefaultRequestBuilder(ContextParamStore contextStore, FormParamStore paramStore) {
        this(contextStore,paramStore,new RequestLogInfo());
    }

    public DefaultRequestBuilder(ContextParamStore contextStore, FormParamStore paramStore, RequestLogInfo logInfo) {
        this.contextStore = contextStore;
        this.paramStore = paramStore;
        this.logInfo = logInfo;
    }

    private List<Param> parseExpressionOfParameters(List<Param> params) {
        List<Param> parsedParams=new ArrayList<>();
        int size = params.size();
        for (int i = 0; i < size; i++) {
            Param param = params.get(i);
            if (param.getValue().contains(templateIdentifier)) {
                String parsedValue = contextStore.getExpressionParser().parse(param.getValue(), String.class);
                if(parsedValue==null){
                    parsedValue= FormParamStore.NULL_VALUE;
                }
                param = new Param(param.getName(),parsedValue);
            }
            parsedParams.add(param);
        }
        return parsedParams;
    }

    /**
     * 获取解析后的Url，同时解析RequestInfo中的参数
     * @param requestInfo
     * @return
     */
    public String buildUrl(RequestInfo requestInfo){
        String url = requestInfo.getUrl();
        //解析url中的表达式
        url = contextStore.getExpressionParser().parse(url, String.class);
        if(url == null){
            throw new RuntimeExecuteException("url "+requestInfo.getUrl()+" parsed as null");
        }
        String method = requestInfo.getHttpMethod().toUpperCase();
        //Get方法时将参数拼接到url上
        if(method.equals("GET")){
            //解析参数中的表达式
            url = fillUrlParams(url, parseExpressionOfParameters(requestInfo.getParams()));
        }
        return url;
    }

    @Override
    public Request buildRequest(RequestInfo requestInfo){
        logger.debug("start build request "+requestInfo);

        String method = requestInfo.getHttpMethod().toUpperCase();

        String url = buildUrl(requestInfo);

        Request request = createRequest(method, url);
        if(!method.equals("GET")){
            //非Get方法时将参数添加到请求中
            fillParams(request, parseExpressionOfParameters(requestInfo.getParams()));
        }

        //添加请求Headr
        setRequestHeader(requestInfo, request);

        //添加请求代理
        setRequestProxy(requestInfo.getProxyInfo(), request);

        logger.debug("finish build request "+ WrapUtil.enAngleBrackets(request.toString(),true));
        return request;
    }

    private Request createRequest(String method, String url) {
        Request request;
        if(method.equals("POST")){
            request= Request.Post(url);
        }else if(method.equals("PUT")){
            request= Request.Put(url);
        }else if(method.equals("DELETE")){
            request= Request.Delete(url);
        }else{
            request= Request.Get(url);
        }
        return request;
    }

    private void setRequestHeader(RequestInfo requestInfo, Request request) {
        Header[] headers = requestInfo.getHeaders().toArray(new Header[]{});
        if(logInfo.isShowFilledHeaders()){
            for (int i = 0; i < headers.length; i++) {
                Header header = headers[i];
                if(header.getValue().contains("$")){
                    headers[i] = new BasicHeader(header.getName(),getExpressionParser().parse(header.getValue(),String.class));
                }
                logger.debug("filled header < "+headers[i]+" >");
            }
        }
        request.setHeaders(headers);
        request.setHeader(new BasicHeader("User-Agents","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400"));
    }

    private void setRequestProxy(ProxyInfo proxyInfo, Request request) {
        if(proxyInfo != null){
            HttpHost proxy = proxyInfo.getProxy();
            String proxysContextKey = proxyInfo.getProxysContextKey();
            if(proxy == null && proxysContextKey != null){
                proxy = getProxyInContext(proxysContextKey);
            }
            if(proxy != null){
                logger.debug("request via proxy < "+proxy+" >");
                request.viaProxy(proxy);
            }
        }
    }

    private HttpHost getProxyInContext(String proxysContextKey){
        Object value = contextStore.getValue(proxysContextKey);
        if(value == null){
            logger.warn("proxysContextKey stored object is null");
            return null;
        }
        if(value instanceof List){
            List proxys = (List) value;
            if(proxys.size()>0){
                int rand = new Random().nextInt(proxys.size());
                Object v = proxys.get(rand);
                if(v instanceof HttpHost){
                    return (HttpHost) v;
                }
            }else{
                logger.warn("proxysContextKey stored list is empty");
            }
        }else{
            logger.warn("proxysContextKey stored object should be type of List<HttpHost>");
        }
        return null;
    }

    private String fillUrlParams(String url,List<Param> params) {
        if(params.size()==0){
            return url;
        }
        boolean hasParam=true;
        if (!(url.contains("?") && url.indexOf("?")<url.indexOf("="))) {
            url+="?";
            hasParam=false;
        }
        StringBuilder urlBuilder = new StringBuilder(url);
        for (Param param : params) {
            String value=param.getValue();
            if(!value.contains("$")){
                hasParam = appendParam(hasParam,urlBuilder,param);
                continue;
            }

            value = paramStore.getValue(param.getKey());

            if (checkNullValue(value)) {
                logger.warn("null value param "+param+" founded");
                continue;
            }

            //如果paramStore有，则创建新的param对象
            hasParam = appendParam(hasParam,urlBuilder,new Param(param.getName(),value));
        }
        urlBuilder.delete(urlBuilder.length()-1,urlBuilder.length());
        return  urlBuilder.toString();
    }

    private boolean checkNullValue(String value) {
        if(value == null || FormParamStore.NULL_VALUE.equals(value)){
            return true;
        }
        return false;
    }

    private boolean appendParam(boolean hasParam, StringBuilder urlBuilder, Param param) {
        if(hasParam){
            urlBuilder.append("&");
        }

        urlBuilder.append(param.getName()).append("=").append(param.getValue()).append("&");

        if (logInfo.isShowFilledParams()) {
            logger.debug("param "+param+" filled");
        }
        return false;
    }

    private List<Param> getFilledParams(List<Param> params) {
        List<Param> filledParams=new ArrayList<>(params.size());
        for (Param param : params) {
            String value=param.getValue();

            if(!value.equals(FormParamStore.NULL_VALUE)){
                filledParams.add(param);
                if (logInfo.isShowFilledParams()) {
                    logger.debug("param "+param+" filled");
                }
                continue;
            }

            value = paramStore.getValue(param.getKey());

            if (checkNullValue(value)) {
                logger.warn("null value param "+param+" founded");
                continue;
            }

            //如果paramStore有，则创建新的param对象
            filledParams.add(new Param(param.getName(),value));

            if (logInfo.isShowFilledParams()) {
                logger.debug("param "+param+" filled");
            }
        }
        return filledParams;
    }

    private Request fillParams(Request request, List<Param> params) {
        return request.bodyForm(getFilledParams(params),Charset.forName("UTF-8"));
    }

    public ContextExpressionParser getExpressionParser(){
        return contextStore.getExpressionParser();
    }

    public void setLogInfo(RequestLogInfo logInfo) {
        this.logInfo = logInfo;
    }
}

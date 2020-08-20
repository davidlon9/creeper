package com.davidlong.http.execution.request;

import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.context.FormParamStore;
import com.davidlong.http.expression.ContextExpressionParser;
import com.davidlong.http.model.Param;
import com.davidlong.http.model.log.RequestLogInfo;
import com.davidlong.http.model.seq.RequestEntity;
import com.davidlong.http.model.seq.RequestInfo;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DefaultRequestBuilder implements HttpRequestBuilder {
    private ExecutionContext context;

    public static final String templateIdentifier="${";

    private static Logger logger=Logger.getLogger(DefaultRequestBuilder.class);

    public DefaultRequestBuilder(ExecutionContext context) {
        this.context = context;
    }

    private List<Param> parseExpressionOfParameters(List<Param> params,ContextExpressionParser expressionParser) {
        List<Param> parsedParams=new ArrayList<>();
        int size = params.size();
        for (int i = 0; i < size; i++) {
            Param param = params.get(i);
            if (param.getValue().contains(templateIdentifier)) {
                String parsedValue = expressionParser.parse(param.getValue(), String.class);
                if(parsedValue==null){
                    parsedValue=FormParamStore.NULL_VALUE;
                }
                param = new Param(param.getName(),parsedValue);
            }
            parsedParams.add(param);
        }
        return parsedParams;
    }

    @Override
    public Request buildRequest(RequestEntity requestEntity){
        RequestInfo requestInfo = requestEntity.getRequestInfo();
        String method = requestInfo.getHttpMethod().toUpperCase();
        Request request;
        String url = requestInfo.getUrl();
        List<Param> params = requestInfo.getParams();

        ContextExpressionParser expressionParser = context.getExpressionParser();
        //解析url中的表达式
        url = expressionParser.parse(url, String.class);
        if(url==null){
            return null;
        }
        //解析参数中的表达式
        params = parseExpressionOfParameters(params,expressionParser);

        if(requestEntity.getName().equals("getPdfDownloadUrls")){
            System.out.println();
        }
        RequestLogInfo requestLogInfo = requestEntity.getRequestLogInfo();
        if(method.equals("POST")){
            request=Request.Post(url);
        }else if(method.equals("PUT")){
            request=Request.Put(url);
        }else if(method.equals("DELETE")){
            request=Request.Delete(url);
        }else{
            request=Request.Get(fillUrlParams(url, params, requestLogInfo));
        }
        if(!method.equals("GET")){
            fillParams(request, params , requestLogInfo);
        }

        Header[] headers = requestInfo.getHeaders().toArray(new Header[]{});
        if(requestLogInfo.isShowFilledHeaders()){
            for (Header header : headers) {
                logger.info("filled header < "+header+" >");
            }
        }
        request.setHeaders(headers);
        request.setHeader(new BasicHeader("User-Agents","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400"));
        return request;
    }

    private String fillUrlParams(String url,List<Param> params,RequestLogInfo logInfo) {
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
                appendParam(hasParam,urlBuilder,param,logInfo);
                continue;
            }

            FormParamStore paramStore = context.getParamStore();
            String contextKey = param.getGlobalKey();
            //先从paramStore中获取value
            if(contextKey!=null && !"".equals(contextKey)){
                value = paramStore.getValue(contextKey);
            }else{
                value = paramStore.getValue(param.getName());
            }

            if(value==null||FormParamStore.NULL_VALUE.equals(value)){
                logger.warn("null value param "+param+" founded");
                continue;
            }

            //如果paramStore有，则创建新的param对象
            if(value != null){
                param = new Param(param.getName(),value);
            }

            hasParam = appendParam(hasParam,urlBuilder,param,logInfo);
        }
        urlBuilder.delete(urlBuilder.length()-1,urlBuilder.length());
        return  urlBuilder.toString();
    }

    private boolean appendParam(boolean hasParam, StringBuilder urlBuilder, Param param, RequestLogInfo logInfo) {
        if(hasParam){
            urlBuilder.append("&");
            hasParam=false;
        }

        urlBuilder.append(param.getName()).append("=").append(param.getValue()).append("&");

        if (logInfo.isShowFilledParams()) {
            logger.info("param "+param+" filled");
        }
        return hasParam;
    }

    private List<Param> getFilledParams(List<Param> params, RequestLogInfo logInfo) {
        List<Param> filledParams=new ArrayList<>(params.size());
        FormParamStore paramStore = context.getParamStore();
        for (Param param : params) {
            String value=param.getValue();
            String globalKey = param.getGlobalKey();
            if(!value.equals(FormParamStore.NULL_VALUE)){
                filledParams.add(param);
                if (logInfo.isShowFilledParams()) {
                    logger.info("param "+param+" filled");
                }
                continue;
            }

            //先从paramStore中获取value
            if(globalKey!=null && !"".equals(globalKey)){
                value = paramStore.getValue(globalKey);
            }else{
                value = paramStore.getValue(param.getName());
            }

            if(value==null||FormParamStore.NULL_VALUE.equals(value)){
                logger.warn("null value param "+param+" founded");
                continue;
            }

            //如果paramStore有，则创建新的param对象
            if(value != null){
                param = new Param(param.getName(),value);
            }

            filledParams.add(new Param(param.getName(),param.getValue()));

            if (logInfo.isShowFilledParams()) {
                logger.info("param "+param+" filled");
            }
        }
        return filledParams;
    }

    private Request fillParams(Request request, List<Param> params, RequestLogInfo logInfo) {
        List<Param> filledParams=new ArrayList<>(params.size());
        FormParamStore paramStore = context.getParamStore();
        for (Param param : params) {
            String value=param.getValue();
            String globalKey = param.getGlobalKey();
            if(!value.equals(FormParamStore.NULL_VALUE)){
                filledParams.add(param);
                if (logInfo.isShowFilledParams()) {
                    logger.info("param "+param+" filled");
                }
                continue;
            }

            //先从paramStore中获取value
            if(globalKey!=null && !"".equals(globalKey)){
                value = paramStore.getValue(globalKey);
            }else{
                value = paramStore.getValue(param.getName());
            }

            if(value==null||FormParamStore.NULL_VALUE.equals(value)){
                logger.warn("null value param "+param+" founded");
                continue;
            }

            //如果paramStore有，则创建新的param对象
            if(value != null){
                param = new Param(param.getName(),value);
            }

            filledParams.add(new Param(param.getName(),param.getValue()));

            if (logInfo.isShowFilledParams()) {
                logger.info("param "+param+" filled");
            }
        }
        return request.bodyForm(filledParams,Charset.forName("UTF-8"));
    }
}

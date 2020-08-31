package com.dlong.creeper.execution.request;

import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.context.FormParamStore;
import com.dlong.creeper.expression.ContextExpressionParser;
import com.dlong.creeper.model.Param;
import com.dlong.creeper.model.log.RequestLogInfo;
import com.dlong.creeper.model.seq.RequestInfo;
import com.dlong.creeper.resolver.util.WrapUtil;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DefaultRequestBuilder implements HttpRequestBuilder {
    private ContextParamStore contextStore;
    private FormParamStore paramStore;
    private RequestLogInfo logInfo;

    public static final String templateIdentifier="${";

    private static Logger logger=Logger.getLogger(DefaultRequestBuilder.class);

    public DefaultRequestBuilder(ExecutionContext context) {
        this(context.getContextStore(),context.getParamStore());
    }

    public DefaultRequestBuilder(ContextParamStore contextStore, FormParamStore paramStore) {
        this(contextStore,paramStore,new RequestLogInfo());
    }

    public DefaultRequestBuilder(ContextParamStore contextStore, FormParamStore paramStore,RequestLogInfo logInfo) {
        this.contextStore = contextStore;
        this.paramStore = paramStore;
        this.logInfo = logInfo;
    }

    private List<Param> parseExpressionOfParameters(List<Param> params, ContextExpressionParser expressionParser) {
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
    public Request buildRequest(RequestInfo requestInfo){
        logger.info("start build request "+requestInfo);

        String method = requestInfo.getHttpMethod().toUpperCase();
        Request request;
        String url = requestInfo.getUrl();
        List<Param> params = requestInfo.getParams();

        ContextExpressionParser expressionParser = contextStore.getExpressionParser();
        //解析url中的表达式
        url = expressionParser.parse(url, String.class);
        if(url == null){
            throw new RuntimeExecuteException("url "+requestInfo.getUrl()+" parsed as null");
        }
        //解析参数中的表达式
        params = parseExpressionOfParameters(params,expressionParser);

        if(method.equals("POST")){
            request=Request.Post(url);
        }else if(method.equals("PUT")){
            request=Request.Put(url);
        }else if(method.equals("DELETE")){
            request=Request.Delete(url);
        }else{
            request=Request.Get(fillUrlParams(url, params));
        }
        if(!method.equals("GET")){
            fillParams(request, params);
        }

        Header[] headers = requestInfo.getHeaders().toArray(new Header[]{});
        if(logInfo.isShowFilledHeaders()){
            for (Header header : headers) {
                logger.info("filled header < "+header+" >");
            }
        }
        request.setHeaders(headers);
        request.setHeader(new BasicHeader("User-Agents","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400"));

        logger.info("finish build request "+ WrapUtil.enAngleBrackets(request.toString(),true) +"\n");
        return request;
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
                appendParam(hasParam,urlBuilder,param);
                continue;
            }

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
            hasParam = appendParam(hasParam,urlBuilder,new Param(param.getName(),value));
        }
        urlBuilder.delete(urlBuilder.length()-1,urlBuilder.length());
        return  urlBuilder.toString();
    }

    private boolean appendParam(boolean hasParam, StringBuilder urlBuilder, Param param) {
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

    private List<Param> getFilledParams(List<Param> params) {
        List<Param> filledParams=new ArrayList<>(params.size());
        for (Param param : params) {
            String value=param.getValue();
//            if(!param.isGlobal()){
//                if(value.equals(FormParamStore.NULL_VALUE)){
//                    logger.warn("non global param value is null，please add in FormParamStore");
//                }
//            }

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

            if(value==null || FormParamStore.NULL_VALUE.equals(value)){
                logger.warn("null value param "+param+" founded");
                continue;
            }

            //如果paramStore有，则创建新的param对象
            filledParams.add(new Param(param.getName(),value));

            if (logInfo.isShowFilledParams()) {
                logger.info("param "+param+" filled");
            }
        }
        return filledParams;
    }

    private Request fillParams(Request request, List<Param> params) {
        return request.bodyForm(getFilledParams(params),Charset.forName("UTF-8"));
    }

    public void setLogInfo(RequestLogInfo logInfo) {
        this.logInfo = logInfo;
    }
}

package com.dlong.creeper.execution.handler;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.HttpConst;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.handler.info.JsonResultCookieInfo;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.util.JSONUtil;
import com.dlong.creeper.util.ResultUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CookieResultHandler implements RequestExecutionResultHandler{
    private static Logger logger = Logger.getLogger(CookieResultHandler.class);

    private static final Map<String,String> cookieCaches = new ConcurrentHashMap<>();

    private volatile static CookieResultHandler singleton;

    private CookieResultHandler() {
    }

    public static CookieResultHandler getInstance() {
        if (singleton == null) {
            synchronized (CookieResultHandler.class) {
                if (singleton == null) {
                    singleton = new CookieResultHandler();
                }
            }
        }
        return singleton;
    }

    /**
     * 在执行前，判断是否有Cookie缓存，有则直接跳过执行
     */
    @Override
    public void beforeExecute(ExecutionResult<? extends RequestEntity> executionResult,ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = executionResult.getOrginalSeq();
        List<JsonResultCookieInfo> infoList = requestEntity.getJsonResultCookieInfoList();
        //没有注解JsonResultCookie的话直接跳过
        if(infoList.size()==0){
            return;
        }
        if (isCachedCookie(infoList)) {
            logger.warn("CookieResultHandler has been cached cookie for "+requestEntity+", so request skiped ");
            executionResult.setSkipExecute(true);
        }
    }

    /**
     * 填充json返回值中的cookie到CookieStore
     * @param executionResult
     * @param context
     * @throws ExecutionException
     */
    @Override
    public void afterExecute(ExecutionResult<? extends RequestEntity> executionResult,ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = executionResult.getOrginalSeq();
        List<JsonResultCookieInfo> infoList = requestEntity.getJsonResultCookieInfoList();
        if(infoList.size()==0){
            return;
        }
        CookieStore cookieStore = context.getCookieStore();
        HttpResponse httpResponse = executionResult.getHttpResponse();
        Assert.notNull(httpResponse);

        JSONObject result = ResultUtil.getResult(httpResponse);
        JSONObject entity = result.getJSONObject("entity");
        if(!entity.getString("type").equals(HttpConst.CONTENT_TYPE_JSON)){
            logger.warn(requestEntity+" response content type is not json can't process as cookie");
            return;
        }

        JSONObject body = entity.getJSONObject("body");
        for (JsonResultCookieInfo info : infoList) {
            handleCookie(body,info,cookieStore,context);
        }
    }

    private void handleCookie(JSONObject body, JsonResultCookieInfo info, CookieStore cookieStore,ChainContext context) {
        Object v = JSONUtil.getValue(body, info.getJsonKey());
        String value = v == null ? info.getDefaultValue():v.toString();
        //解析默认值中的表达式
        value = context.getExpressionParser().parse(value, String.class);

        BasicClientCookie basicClientCookie = new BasicClientCookie(info.getName(),value);
        basicClientCookie.setDomain(info.getDomain());
        basicClientCookie.setAttribute("domain",info.getDomain());
        basicClientCookie.setAttribute("path",info.getPath());
        basicClientCookie.setAttribute("expiry",String.valueOf(info.getExpiry()));

        if(info.isCache()){
            cookieCaches.put(info.getName(),value);
            logger.info("CookieResultHandler has been cached cookie "+info.getName());
        }
        cookieStore.addCookie(basicClientCookie);
        logger.info("Add cookie ["+info.getName()+":"+value+"] in CookieStore");
    }

    /**
     * 如果没有注解，则返回false，
     * 否则，检查cachedCookies是否包含cookie name，所有注解的cookie name都在缓存中才返回true
     * 返回true就会跳过执行请求
     * @return
     */
    private boolean isCachedCookie(List<JsonResultCookieInfo> infoList) {
        for (JsonResultCookieInfo resultCookie : infoList) {
            if (!cookieCaches.containsKey(resultCookie.getName())) {
                return false;
            }
        }
        return true;
    }

}

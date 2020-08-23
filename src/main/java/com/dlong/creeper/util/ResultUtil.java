package com.dlong.creeper.util;

import com.alibaba.fastjson.JSONObject;
import com.dlong.creeper.HttpConst;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ResultUtil {
    public static JSONObject getResult(HttpResponse httpResponse,String charSet){
        if(httpResponse==null){
            return null;
        }

        StatusLine statusLine = httpResponse.getStatusLine();
        HttpEntity httpEntity = httpResponse.getEntity();
        String bodyStr = null;
        try {
            bodyStr = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object body = null;
        String type = null;
        String value = httpEntity.getContentType().getValue();
        if(value.contains(HttpConst.CONTENT_TYPE_JSON)){
            type=HttpConst.CONTENT_TYPE_JSON;
            try {
                body = JSONObject.parseObject(bodyStr);
            } catch (Exception e) {
                body = JSONObject.parseObject(bodyStr.substring(bodyStr.indexOf("{"),bodyStr.lastIndexOf("}")+1));
            }
            if (body == null) {
                body = new JSONObject();
            }
        }else if(value.contains(HttpConst.CONTENT_TYPE_TEXT_HTML)){
            type=HttpConst.CONTENT_TYPE_TEXT_HTML;
            body=bodyStr;
        }

        JSONObject result=new JSONObject();

        JSONObject entity=new JSONObject();
        entity.put("type",type);
        entity.put("body",body);
        result.put("entity",entity);

        JSONObject status = new JSONObject();
        status.put("code",statusLine.getStatusCode());
        status.put("reason",statusLine.getStatusCode());
        result.put("status",status);
        return result;
    }

    public static JSONObject getResult(HttpResponse httpResponse){
        return getResult(httpResponse,null);
    }

    public static Object getBody(JSONObject result){
        return result.getJSONObject("entity").get("body");
    }

    public static String getHtmlBody(JSONObject result){
        return (String)getBody(result);
    }

    public static JSONObject getJsonBody(JSONObject result){
        Object body = getBody(result);
        if (body instanceof JSONObject) {
            return (JSONObject) body;
        }else if(body instanceof String){
            try {
                return JSONObject.parseObject((String)body);
            } catch (Exception e) {
            }
        }
        return new JSONObject();
    }

    public static String getStatusCode(JSONObject result){
        return result.getJSONObject("status").getString("code");
    }
}

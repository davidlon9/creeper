package com.davidlong.creeper.util;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
    public static Object getValue(JSONObject target, String path){
        Object result=null;
        if(path.contains(".")){
            String[] split = path.split(".");
            JSONObject json=target;
            for (String key : split) {
                result = json.get(key);
                if(result instanceof JSONObject){
                    json=(JSONObject) result;
                }
            }
        }else{
            result=target.get(path);
        }
        return result;
    }
}

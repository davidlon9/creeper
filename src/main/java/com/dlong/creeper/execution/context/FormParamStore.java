package com.dlong.creeper.execution.context;

import com.dlong.creeper.model.Param;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FormParamStore implements ParamStore<String,String>{
    private final Map<String,String> params = new ConcurrentHashMap<>();
    public static final String NULL_VALUE="$NULL";

    public FormParamStore() {
    }

    public String getValue(String name){
        String v = params.get(name);
        return isNullValue(v) ? null : v;
    }

    public synchronized void addParam(String name, String value){
        params.put(name,value == null ? NULL_VALUE : value);
    }

    private synchronized void addParam(Param param) {
        addParam(param.getKey(),param.getValue());
    }

    public synchronized void addIfNull(Param param) {
        String key=param.getKey();
        if (params.containsKey(key)) {
            String s = params.get(key);
            if(NULL_VALUE.equals(s)){
                addParam(key,param.getValue());
            }
        }else{
            addParam(key,param.getValue());
        }
    }

    public void addParams(Map<String, String> params) {
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            addParam(entry.getKey(),entry.getValue());
        }
    }

    public void addParams(List<Param> params) {
        for (Param param : params) {
            addParam(param);
        }
    }

    public void addParams(Param... params) {
        for (Param param : params) {
            addParam(param);
        }
    }

    public boolean containsName(String name){
        return params.containsKey(name);
    }

    public boolean isNullValue(String value){
        return NULL_VALUE.equals(value);
    }

    public Map<String, String> getParamMap() {
        return params;
    }

    public static void main(String[] args) {
        
    }
}

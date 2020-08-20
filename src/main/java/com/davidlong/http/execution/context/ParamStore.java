package com.davidlong.http.execution.context;

import com.davidlong.http.model.Store;

import java.util.Map;

public interface ParamStore<K,V> extends Store {
    V getValue(K name);

    void addParam(K name, V value);

    void addParams(Map<K,V> params);

    boolean containsName(K name);

    Map<K, V> getParamMap();
}

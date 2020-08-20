package com.davidlong.http.resolver.util;

import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.model.seq.RequestEntity;

public class LogNames {
    public static String request(RequestEntity requestEntity){
        return WrapUtil.enAngleBrackets(requestEntity.getName());
    }

    public static String chain(RequestChainEntity requestChainEntity){
        return WrapUtil.enBrackets(requestChainEntity.getName());
    }

    public static String chain(Class chainClass){
        return WrapUtil.enBrackets(chainClass.getSimpleName());
    }
}

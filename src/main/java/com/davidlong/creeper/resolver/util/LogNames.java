package com.davidlong.creeper.resolver.util;

import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.RequestEntity;

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

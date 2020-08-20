package com.davidlong.creeper.resolver;

import com.davidlong.creeper.model.seq.RequestEntity;
import com.davidlong.creeper.model.seq.RequestInfo;

public interface RequestResolver{
    RequestEntity resolve(RequestInfo requestInfo);
}

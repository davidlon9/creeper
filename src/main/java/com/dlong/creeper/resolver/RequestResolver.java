package com.dlong.creeper.resolver;

import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.RequestInfo;

public interface RequestResolver{
    RequestEntity resolve(RequestInfo requestInfo);
}

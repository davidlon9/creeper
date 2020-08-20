package com.davidlong.http.resolver;

import com.davidlong.http.model.seq.RequestEntity;
import com.davidlong.http.model.seq.RequestInfo;

public interface RequestResolver{
    RequestEntity resolve(RequestInfo requestInfo);
}

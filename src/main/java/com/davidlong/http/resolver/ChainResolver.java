package com.davidlong.http.resolver;

import com.davidlong.http.model.seq.RequestChainEntity;

public interface ChainResolver{
    RequestChainEntity resolveChain();
}

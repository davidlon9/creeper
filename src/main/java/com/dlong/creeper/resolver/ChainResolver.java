package com.dlong.creeper.resolver;

import com.dlong.creeper.model.seq.RequestChainEntity;

public interface ChainResolver {
   RequestChainEntity resolve(Class<?> chainClass);
}

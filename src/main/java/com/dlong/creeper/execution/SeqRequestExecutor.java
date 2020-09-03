package com.dlong.creeper.execution;

import com.dlong.creeper.execution.base.BaseRequestExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.seq.SeqRequestEntity;

public class SeqRequestExecutor extends BaseRequestExecutor<SeqRequestEntity> {
    public SeqRequestExecutor(ChainContext context) {
        super(context);
    }
}

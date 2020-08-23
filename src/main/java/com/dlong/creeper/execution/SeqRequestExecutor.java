package com.dlong.creeper.execution;

import com.dlong.creeper.execution.base.BaseRequestExecutor;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.model.seq.SeqRequestEntity;

public class SeqRequestExecutor extends BaseRequestExecutor<SeqRequestEntity> {
    public SeqRequestExecutor(ExecutionContext context) {
        super(context);
    }
}

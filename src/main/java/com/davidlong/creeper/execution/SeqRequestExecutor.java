package com.davidlong.creeper.execution;

import com.davidlong.creeper.execution.base.BaseRequestExecutor;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.seq.SeqRequestEntity;

public class SeqRequestExecutor extends BaseRequestExecutor<SeqRequestEntity> {
    public SeqRequestExecutor(ExecutionContext context) {
        super(context);
    }
}

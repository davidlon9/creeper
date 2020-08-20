package com.davidlong.http.execution;

import com.davidlong.http.execution.base.BaseRequestExecutor;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.model.seq.SeqRequestEntity;

public class SeqRequestExecutor extends BaseRequestExecutor<SeqRequestEntity> {
    public SeqRequestExecutor(ExecutionContext context) {
        super(context);
    }
}

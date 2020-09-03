package com.dlong.creeper.execution.context;

import com.dlong.creeper.util.TimeUtil;

import java.util.Map;

public class ContextRootObject {
    private final TimeUtil time;

    private final Map<String,Object> context;

    public ContextRootObject(ContextParamStore context) {
        this.time=new TimeUtil();
        this.context=context.getParamMap();
        init();
    }

    private void init() {
    }

    public TimeUtil getTime() {
        return time;
    }

    public Map<String,Object> getContext() {
        return context;
    }
}

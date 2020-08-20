package com.davidlong.creeper.execution.multi;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class MultiEntityThreadFactory extends CustomizableThreadFactory {
    public MultiEntityThreadFactory(String threadName) {
        super(threadName+"-");
    }

    public MultiEntityThreadFactory(String groupName,String threadName) {
        super(threadName+"-");
        super.setThreadGroupName(groupName);
    }

    @Override
    public void setThreadNamePrefix(String threadNamePrefix) {
        super.setThreadNamePrefix(threadNamePrefix+"-");
    }

    @Override
    public String getThreadNamePrefix() {
        if(getThreadGroup()!=null && !"main".equals(getThreadGroup().getName())){
            return getThreadGroup().getName()+"-"+super.getThreadNamePrefix();
        }
        return super.getThreadNamePrefix();
    }
}

package com.davidlong.creeper.model.seq.multi;

import com.davidlong.creeper.execution.handler.help.ThreadLocalMap;
import com.davidlong.creeper.model.seq.RequestEntity;

import java.util.concurrent.ExecutorService;

public class MultiRequestEntity extends RequestEntity implements Multiple{
    private int threadSize;
    private boolean shareContext = true;
    private boolean moveStopAll=true;
    private ThreadLocalMap<ExecutorService> localThreadPool = new ThreadLocalMap<>();

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public boolean isShareContext() {
        return shareContext;
    }

    public void setShareContext(boolean shareContext) {
        this.shareContext = shareContext;
    }

    public void setMoveStopAll(boolean moveStopAll) {
        this.moveStopAll = moveStopAll;
    }

    @Override
    public String toString() {
        return "["+ index +"--"+name+"--"+description+"]";
    }

    @Override
    public ThreadLocalMap<ExecutorService> getLocalThreadPool() {
        return this.localThreadPool;
    }

    public boolean isMoveStopAll() {
        return moveStopAll;
    }

    @Override
    public void shutdown() {
        if (this.localThreadPool != null) {
            this.localThreadPool.get().shutdown();
        }
    }

    @Override
    public boolean isShutdown() {
        if (this.localThreadPool != null) {
           return this.localThreadPool.get().isShutdown();
        }
        return false;
    }

    public void setLocalThreadPool(ExecutorService localThreadPool) {
        this.localThreadPool.set(localThreadPool);
    }


}

package com.davidlong.http.model.seq.multi;

import com.davidlong.http.execution.handler.help.ThreadLocalMap;
import com.davidlong.http.model.ExecutionStrategy;
import com.davidlong.http.model.Multiple;
import com.davidlong.http.model.seq.RequestChainEntity;

import java.util.concurrent.ExecutorService;

public class MultiRequestChainEntity extends RequestChainEntity implements Multiple {
    private int threadSize;
    private int interval;
    private boolean shareContext = true;
    private boolean moveStopAll = true;
    private ExecutionStrategy strategy = ExecutionStrategy.Parallel;
    private ThreadLocalMap<ExecutorService> localThreadPool =new ThreadLocalMap<>();

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize){
        this.threadSize = threadSize;
    }

    public ExecutionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ExecutionStrategy strategy) {
        this.strategy = strategy;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isShareContext() {
        return shareContext;
    }

    public void setShareContext(boolean shareContext) {
        this.shareContext = shareContext;
    }

    @Override
    public ThreadLocalMap<ExecutorService> getLocalThreadPool() {
        return this.localThreadPool;
    }

    public boolean isMoveStopAll() {
        return moveStopAll;
    }

    public void setLocalThreadPool(ExecutorService localThreadPool) {
        this.localThreadPool.set(localThreadPool);
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

    public void setMoveStopAll(boolean moveStopAll) {
        this.moveStopAll = moveStopAll;
    }
}

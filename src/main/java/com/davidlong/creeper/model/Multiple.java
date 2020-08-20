package com.davidlong.creeper.model;

import com.davidlong.creeper.execution.handler.help.ThreadLocalMap;

import java.util.concurrent.ExecutorService;

public interface Multiple {
    ThreadLocalMap<ExecutorService> getLocalThreadPool();

    boolean isMoveStopAll();

    void shutdown();

    boolean isShutdown();
}


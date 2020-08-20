package com.davidlong.http.model;

import com.davidlong.http.execution.handler.help.ThreadLocalMap;

import java.util.concurrent.ExecutorService;

public interface Multiple {
    ThreadLocalMap<ExecutorService> getLocalThreadPool();

    boolean isMoveStopAll();

    void shutdown();

    boolean isShutdown();
}


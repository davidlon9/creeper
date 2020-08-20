package com.davidlong.creeper.execution.handler.help;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalMap<T> extends InheritableThreadLocal<T> {
    private Map<Thread,T> threadLocalMap=new ConcurrentHashMap<>();

    @Override
    public void set(T value) {
        super.set(value);
        threadLocalMap.put(Thread.currentThread(),value);
    }

    public T get(Thread thread){
        return threadLocalMap.get(thread);
    }

    public Map<Thread, T> getThreadLocalMap() {
        return threadLocalMap;
    }
}

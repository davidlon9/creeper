package com.dlong.creeper.model.seq;

import com.dlong.creeper.execution.handler.help.ThreadLocalMap;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;

public class LoopableEntity extends HandleableEntity{
    private Looper looper;
    private UrlRecorder recorder;
    private ThreadLocalMap<Thread> parallelLocalThread = new ThreadLocalMap<>();

    public Looper getLooper() {
        return looper;
    }

    public void setLooper(Looper looper) {
        this.looper = looper;
    }

    public UrlRecorder getRecorder() {
        return recorder;
    }

    public void setRecorder(UrlRecorder recorder) {
        this.recorder = recorder;
    }

    public Thread getParalleThread() {
        return parallelLocalThread.get();
    }

    public Thread getParallelThread(Thread localThread) {
        return parallelLocalThread.get(localThread);
    }

    public void setParallelThread(Thread parallelLocalThread) {
        this.parallelLocalThread.set(parallelLocalThread);
    }

    public boolean isUrlRecorded(String historicalUrl){
        return this.recorder!=null && this.recorder.isUrlRecorded(historicalUrl);
    }
}
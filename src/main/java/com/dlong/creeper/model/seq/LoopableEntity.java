package com.dlong.creeper.model.seq;

import com.dlong.creeper.execution.handler.help.ThreadLocalMap;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.recorder.UrlRecorder;

public class LoopableEntity extends HandleableEntity{
    private Looper looper;
    private UrlRecorder urlRecorder;
    private ThreadLocalMap<Thread> parallelLocalThread = new ThreadLocalMap<>();

    public Looper getLooper() {
        return looper;
    }

    public void setLooper(Looper looper) {
        this.looper = looper;
    }

    public UrlRecorder getRecorder() {
        return urlRecorder;
    }

    public void setRecorder(UrlRecorder urlRecorder) {
        this.urlRecorder = urlRecorder;
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
        return this.urlRecorder !=null && this.urlRecorder.isUrlRecorded(historicalUrl);
    }
}

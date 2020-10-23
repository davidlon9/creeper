package com.dlong.creeper.model.seq.control;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PredictableUrlLooper extends Looper{
    private boolean isPredicted = false;
    private Map<String,Object> predictUrlItemMap =new ConcurrentHashMap<>();

    public Map<String,Object> getPredictUrlItemMap() {
        return predictUrlItemMap;
    }

    public void putPredictUrlItem(String url, Object item) {
        this.predictUrlItemMap.put(url,item);
    }

    public Collection<Object> getPredictItems() {
        return this.predictUrlItemMap.values();
    }

    public Set<String> getPredictUrls() {
        return this.predictUrlItemMap.keySet();
    }

    public boolean isPredicted() {
        return isPredicted;
    }

    public void setPredicted(boolean predicted) {
        isPredicted = predicted;
    }
}

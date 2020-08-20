package com.davidlong.creeper.model.seq;

import java.util.HashSet;
import java.util.Set;

public class UniqueRequestEntity extends SeqRequestEntity {
    private Set<String> historyUrlSet = new HashSet<>(16);

    public Set<String> getHistoryUrlSet() {
        return historyUrlSet;
    }

    public void addHistoryUrl(String historyUrl) {
        this.historyUrlSet.add(historyUrl);
    }

    public boolean isHistoryUrl(String historyUrl) {
        return this.historyUrlSet.contains(historyUrl);
    }
}

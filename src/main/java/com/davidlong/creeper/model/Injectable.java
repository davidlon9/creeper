package com.davidlong.creeper.model;

public interface Injectable {
    Class getStoreClass();

    String getKey();

    String getTarget();
}

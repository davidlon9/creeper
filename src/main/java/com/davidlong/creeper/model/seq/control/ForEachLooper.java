package com.davidlong.creeper.model.seq.control;


public class ForEachLooper extends Looper {
    private String itemsContextKey;
    private String itemName;

    public String getItemsContextKey() {
        return itemsContextKey;
    }

    public void setItemsContextKey(String itemsContextKey) {
        this.itemsContextKey = itemsContextKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}

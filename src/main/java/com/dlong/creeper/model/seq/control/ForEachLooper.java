package com.dlong.creeper.model.seq.control;


public class ForEachLooper extends PredictableUrlLooper {
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

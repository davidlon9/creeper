package com.davidlong.creeper.model;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Param extends BasicNameValuePair{
    private String globalKey;

    public Param(String name, String value) {
        super(name, value);
    }

    public Param(String name, Object value) {
        super(name, value.toString());
    }

    public String getGlobalKey() {
        return globalKey;
    }

    public void setGlobalKey(String globalKey) {
        this.globalKey = globalKey;
    }

    @Override
    public int hashCode() {
        return 31*this.getName().hashCode()+globalKey!=null?globalKey.hashCode():0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof NameValuePair)) {
            return false;
        } else {
            BasicNameValuePair that = (BasicNameValuePair)object;
            return this.getName().equals(that.getName());
        }
    }

    public String getKey() {
        return this.globalKey!=null?this.globalKey:this.getName();
    }

    @Override
    public String toString() {
        return "< "+ super.getName() +" = "+ super.getValue() +" >";
    }
}

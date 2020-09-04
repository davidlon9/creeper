package com.dlong.creeper.model;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Param extends BasicNameValuePair{
    private String globalKey;
    private String uniqueKey;

    public Param(String name, String value) {
        super(name, value);
    }

    public Param(String name, Object value) {
        super(name, value.toString());
    }

    @Override
    public int hashCode() {
        int result = this.getName().hashCode();
        result = 31 * result + (uniqueKey != null ? uniqueKey.hashCode() : 0);
        result = 31 * result + (globalKey != null ? globalKey.hashCode() : 0);
        return result;
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
        String uniqueKey = this.uniqueKey;
        if(uniqueKey!=null && !"".equals(uniqueKey)){
            return uniqueKey;
        }
        String globalKey = this.globalKey;
        if(globalKey !=null && !"".equals(globalKey)){
            return globalKey;
        }
        return super.getName();
    }

    public String getGlobalKey() {
        return globalKey;
    }

    public void setGlobalKey(String globalKey) {
        this.globalKey = globalKey;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    @Override
    public String toString() {
        return "< "+ super.getName() +" = "+ super.getValue() +" >";
    }
}

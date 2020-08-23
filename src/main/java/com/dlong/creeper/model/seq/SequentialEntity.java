package com.dlong.creeper.model.seq;

import java.util.Comparator;

public class SequentialEntity{
    public static final Comparator<SequentialEntity> INDEX_COMPARATOR = (Comparator<SequentialEntity>) (o1, o2) -> {
        if(o1 == null){
            return -1;
        }else if(o2 == null){
            return 1;
        }else if(o1 == o2){
            return 0;
        }else return Integer.compare(o1.getIndex(), o2.getIndex());
    };

    protected String sequenceId;
    protected String fullName;
    protected int index;
    protected String name;
    protected String description;
    protected RequestChainEntity parent;
    protected RequestChainEntity refParent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public RequestChainEntity getParent() {
        return parent;
    }

    public RequestChainEntity getRefParent() {
        return refParent;
    }

    public void setRefParent(RequestChainEntity refParent) {
        this.refParent = refParent;
    }

    public void setParent(RequestChainEntity parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("[");
        sb.append(index);
        sb.append("--");
        sb.append(name);
        if(!"".equals(description)){
            sb.append("--");
            sb.append(description);
        }
        sb.append("]");
        return sb.toString();
    }
}

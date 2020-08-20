package com.davidlong.http.annotation.control;

public class FailedStrategy {
    public static final int defualtFailedWait=50;
    public static final FailedStrategy BACK=new FailedStrategy("BACK");
    public static final FailedStrategy FORWARD=new FailedStrategy("FORWARD");
    public static final FailedStrategy RESTART=new FailedStrategy("RESTART");
    public static final FailedStrategy RETRY=new FailedStrategy("RETRY");
    public static final FailedStrategy TERMINATE=new FailedStrategy("TERMINATE");

    public static final FailedStrategy MUTABLE=new FailedStrategy("MUTABLE");

    public static final FailedStrategy SUCCESS=new FailedStrategy("SUCCESS");

    static {
        RETRY.setWait(defualtFailedWait);
    }

    private String name;
    private Integer to;
    private Integer wait=defualtFailedWait;

    private FailedStrategy(String name, Integer to) {
        this.name = name;
        this.to = to;
    }

    public Integer getWait() {
        return wait;
    }

    public void setWait(Integer wait) {
        this.wait = wait;
    }

    private FailedStrategy(String name) {
        this.name = name;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public static FailedStrategy JUMP(Integer to){
        return new FailedStrategy("JUMP",to);
    }

    @Override
    public String toString() {
        return "FailedStrategy["+name+"]";
    }
}

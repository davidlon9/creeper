package com.dlong.creeper.annotation.control;

public class ExceptionStrategy {
    public static final int defualtFailedWait=50;
    public static final ExceptionStrategy BACK=new ExceptionStrategy("BACK");
    public static final ExceptionStrategy FORWARD=new ExceptionStrategy("FORWARD");
    public static final ExceptionStrategy RESTART=new ExceptionStrategy("RESTART");
    public static final ExceptionStrategy RETRY=new ExceptionStrategy("RETRY");
    public static final ExceptionStrategy TERMINATE=new ExceptionStrategy("TERMINATE");

    public static final ExceptionStrategy MUTABLE=new ExceptionStrategy("MUTABLE");

    public static final ExceptionStrategy SUCCESS=new ExceptionStrategy("SUCCESS");

    static {
        RETRY.setWait(defualtFailedWait);
    }

    private String name;
    private Integer to;
    private Integer wait=defualtFailedWait;

    private ExceptionStrategy(String name, Integer to) {
        this.name = name;
        this.to = to;
    }

    public Integer getWait() {
        return wait;
    }

    public void setWait(Integer wait) {
        this.wait = wait;
    }

    private ExceptionStrategy(String name) {
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

    public static ExceptionStrategy JUMP(Integer to){
        return new ExceptionStrategy("JUMP",to);
    }

    @Override
    public String toString() {
        return "ExceptionStrategy["+name+"]";
    }
}

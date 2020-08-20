package com.davidlong.http.control;

import org.springframework.util.Assert;

public class JumpAction extends IntervalAction {
    private Object jumpTo;
    //jump name
    //1.name : 全局查找，如果找到一个则返回，如果是多个则抛出异常
    //2.name1-name2 : 全路径查找，一定要写全，适用于两个chain内有同一个name的情况
    JumpAction() {
        super("JUMP");
    }

    public JumpAction(Object jumpTo) {
        this();
        this.jumpTo = jumpTo;
    }

    public JumpAction(Object jumpTo, int interval) {
        super("JUMP",interval);
        this.jumpTo = jumpTo;
    }

    @Override
    public Object nextSequential() {
        Assert.notNull(jumpTo);
        return jumpTo;
    }

    public Object getJumpTo() {
        return jumpTo;
    }

    @Override
    public int hashCode() {
        return 31*super.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        } else if (!(obj instanceof JumpAction)) {
            return false;
        } else {
            JumpAction that = (JumpAction) obj;
            return this.getName().equals(that.getName());
        }
    }

}

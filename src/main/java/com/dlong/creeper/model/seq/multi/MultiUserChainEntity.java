package com.dlong.creeper.model.seq.multi;

import com.dlong.creeper.exception.RuntimeResolveException;

import java.util.List;

public class MultiUserChainEntity extends MultiRequestChainEntity{
    private List<LoginUserInfo> users;

    public List<LoginUserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<LoginUserInfo> users) {
        this.users = users;
    }

    @Override
    public void setThreadSize(int threadSize){
        throw new RuntimeResolveException("MultiUserChainEntity threadSize field rely on users-field's size");
    }

    @Override
    public int getThreadSize() {
        return users.size();
    }

    @Override
    public boolean isMoveStopAll() {
        return false;
    }
}

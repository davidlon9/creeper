package com.davidlong.creeper.resolver;

import com.davidlong.creeper.annotation.seq.multi.MultiUserChain;
import com.davidlong.creeper.annotation.seq.multi.LoginUser;
import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.model.seq.multi.MultiUserChainEntity;
import com.davidlong.creeper.model.seq.multi.LoginUserInfo;
import com.davidlong.creeper.resolver.base.BaseChainResolver;

import java.util.ArrayList;
import java.util.List;

public class MultiUserChainResolver extends BaseChainResolver implements ChainResolver{
    public MultiUserChainResolver(Class<?> handleClass) {
        super(handleClass, MultiUserChain.class);
    }
    
    public MultiUserChainEntity resolveChain() {
        try {
            MultiUserChainEntity entity=new MultiUserChainEntity();
            super.resolveChain(entity);
            MultiUserChain multiUserChain = (MultiUserChain) super.getAnnotation();
            LoginUser[] loginUsers = multiUserChain.users();
            List<LoginUserInfo> loginUserInfos =new ArrayList<>(loginUsers.length);
            for (LoginUser loginUser : loginUsers) {
                loginUserInfos.add(new LoginUserInfo(loginUser.usernameKey(),loginUser.passwordKey(),loginUser.usernameVal(), loginUser.passwordVal()));
            }
            entity.setUsers(loginUserInfos);
            entity.setInterval(multiUserChain.interval());
            return entity;
        } catch (AnnotationNotFoundException e) {
            return null;
        }
    }
}

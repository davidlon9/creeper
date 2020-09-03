package com.dlong.creeper.resolver;

import com.dlong.creeper.annotation.seq.multi.MultiUserChain;
import com.dlong.creeper.annotation.seq.multi.LoginUser;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.multi.MultiUserChainEntity;
import com.dlong.creeper.model.seq.multi.LoginUserInfo;
import com.dlong.creeper.resolver.base.BaseChainAnnoResolver;

import java.util.ArrayList;
import java.util.List;

public class MultiUserChainResolver extends BaseChainAnnoResolver implements ChainAnnoResolver {
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

package com.davidlong.creeper.execution.multi;

import com.davidlong.creeper.execution.base.BaseChainExecutor;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.ChainExecutionResult;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.seq.multi.LoginUserInfo;
import com.davidlong.creeper.model.seq.multi.MultiUserChainEntity;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultiUserExecutor extends BaseChainExecutor<MultiUserChainEntity> {

    public MultiUserExecutor(ExecutionContext context) {
        super(context,true);
    }

    @Override
    public ChainExecutionResult<MultiUserChainEntity> execute() {
        return null;
    }

    @Override
    public ChainExecutionResult<MultiUserChainEntity> execute(MultiUserChainEntity multiUserChainEntity){
        int threadSize = multiUserChainEntity.getThreadSize();
        CountDownLatch countDownLatch=new CountDownLatch(threadSize);

        List<LoginUserInfo> users = multiUserChainEntity.getUsers();
        for (LoginUserInfo user : users) {
            new Thread(() -> {
                getContext().getParamStore().addParam(user.getPasswordKey(),user.getPasswordVal());
                ExecutionResult<MultiUserChainEntity> result = super.execute(multiUserChainEntity);
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println("MultiChain execution finished!!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

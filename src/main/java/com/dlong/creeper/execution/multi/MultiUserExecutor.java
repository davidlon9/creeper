package com.dlong.creeper.execution.multi;

import com.dlong.creeper.execution.base.BaseChainExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.model.result.ChainExecutionResult;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.seq.multi.LoginUserInfo;
import com.dlong.creeper.model.seq.multi.MultiUserChainEntity;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultiUserExecutor extends BaseChainExecutor<MultiUserChainEntity> {

    public MultiUserExecutor(ChainContext context) {
        //每个线程单独使用一个Context
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

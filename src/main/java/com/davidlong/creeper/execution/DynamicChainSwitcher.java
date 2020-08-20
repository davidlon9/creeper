package com.davidlong.http.execution;

import com.davidlong.http.model.seq.RequestChainEntity;
import org.apache.log4j.Logger;

/**
 * 动态Chain
 */
public abstract class DynamicChainSwitcher {

    private static Logger logger=Logger.getLogger(DynamicChainSwitcher.class);

    protected RequestChainEntity rootChain;//遍历所有chain,初始化参数

    protected RequestChainEntity executingChain;//当前正在执行的chain

    private boolean isInitialed=false;

    public DynamicChainSwitcher(RequestChainEntity rootChain) {
        refreshRootChain(rootChain);
    }

    private void init(){
        if(!isInitialed){
            initInternal();
            this.isInitialed=true;
        }
    }

    public void switchExecutingChain(RequestChainEntity switchToChain) {
        //更新执行中的Chain
        this.executingChain = switchToChain;
        //如果是另一个独立的Chain则使该Chain成为rootChain
        if (!this.rootChain.getSequentialList().contains(switchToChain) && this.executingChain!=rootChain) {
            refreshRootChain(switchToChain);
        }
        afterSwitched(switchToChain);
    }

    private void refreshRootChain(RequestChainEntity chain) {
        this.isInitialed = false;
        this.rootChain = chain;
        this.executingChain = chain;
        init();
    }

    /**
     * 注意在子类覆盖的时候，调用的对象必需初始化，因为此方法在构造时就调用
     */
    public void initInternal() {

    }

    public void afterSwitched(RequestChainEntity switchedChain) {
    }


    public RequestChainEntity getExecutingChain() {
        return executingChain;
    }

    public RequestChainEntity getRootChain() {
        return rootChain;
    }
}

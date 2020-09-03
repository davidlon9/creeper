package com.dlong.creeper.execution.base;

import com.dlong.creeper.execution.context.ChainContext;
import org.apache.log4j.Logger;


public class BaseChainContextExecutor {
    private Logger logger= Logger.getLogger(BaseChainContextExecutor.class);

    private ChainContext context;
    private final boolean isMultiThread;

    private ThreadLocal<ChainContext> contextThreadLocal=new ThreadLocal<ChainContext>(){
        @Override
        protected ChainContext initialValue() {
            try {
                return context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    };


    public BaseChainContextExecutor(ChainContext context) {
        this(context,false);
    }

    public BaseChainContextExecutor(ChainContext context, boolean isMultiThread) {
        this.context = context;
        this.isMultiThread = isMultiThread;
    }


    public ChainContext getContext() {
        if(!isMultiThread){
            return context;
        }
        return getThreadLocalContext();
    }

    public void setContext(ChainContext context) {
        this.context = context;
    }

    private ChainContext getThreadLocalContext() {
        ChainContext chainContext = contextThreadLocal.get();
        logger.debug(Thread.currentThread().getName()+" get context "+ chainContext);
        if(chainContext !=null){
            return chainContext;
        }else{
            try {
                ChainContext clone = this.getContext().clone();
                contextThreadLocal.set(clone);
                return clone;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public boolean isMultiThread() {
        return isMultiThread;
    }
}

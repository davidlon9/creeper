package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.execution.context.ExecutionContext;
import org.apache.log4j.Logger;


public class ContextSeqExecutor{
    private Logger logger= Logger.getLogger(ContextSeqExecutor.class);

    private ExecutionContext context;
    private final boolean isMultiThread;

    private ThreadLocal<ExecutionContext> contextThreadLocal=new ThreadLocal<ExecutionContext>(){
        @Override
        protected ExecutionContext initialValue() {
            try {
                return context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    };


    public ContextSeqExecutor(ExecutionContext context) {
        this(context,false);
    }

    public ContextSeqExecutor(ExecutionContext context, boolean isMultiThread) {
        this.context = context;
        this.isMultiThread = isMultiThread;
    }


    public ExecutionContext getContext() {
        if(!isMultiThread){
            return context;
        }
        return getThreadLocalContext();
    }

    public void setContext(ExecutionContext context) {
        this.context = context;
    }

    private ExecutionContext getThreadLocalContext() {
        ExecutionContext executionContext = contextThreadLocal.get();
        logger.debug(Thread.currentThread().getName()+" get context "+ executionContext);
        if(executionContext !=null){
            return executionContext;
        }else{
            try {
                ExecutionContext clone = this.getContext().clone();
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

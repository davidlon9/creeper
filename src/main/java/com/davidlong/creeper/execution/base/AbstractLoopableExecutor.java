package com.davidlong.creeper.execution.base;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.ExecutorFactory;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.seq.control.Looper;
import com.davidlong.creeper.model.seq.LoopableEntity;

import java.io.IOException;

public abstract class AbstractLoopableExecutor<T extends LoopableEntity> extends ContextSeqExecutor implements LoopableExecutor<T>,Cloneable{

    public AbstractLoopableExecutor(ExecutionContext context) {
        super(context);
    }

    public AbstractLoopableExecutor(ExecutionContext context, boolean isMultiThread) {
        super(context,isMultiThread);
    }

    @Override
    public AbstractLoopableExecutor clone() throws CloneNotSupportedException {
        AbstractLoopableExecutor loopableExecutor = (AbstractLoopableExecutor) super.clone();
        ExecutionContext context = loopableExecutor.getContext();
        ExecutionContext clone = context.clone();
        loopableExecutor.setContext(clone);
        return loopableExecutor;
    }

    /**
     * 默认循环执行
     * @param t
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    @Override
    public ExecutionResult<T> execute(T t) throws IOException, ExecutionException {
        Looper looper = t.getLooper();
        ExecutionResult<T> result;
        //有looper则，使用该looper循环执行，否则正常执行
        if (looper != null) {
            ExecuteLooper<T> executeLooper = ExecutorFactory.createExecuteLooper(looper.getClass(),this);
            result = executeLooper.loop(t);
        }else{
            result = doExecute(t);
        }
        return result;
    }
}

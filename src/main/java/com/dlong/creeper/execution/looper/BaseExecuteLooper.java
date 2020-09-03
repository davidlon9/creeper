package com.dlong.creeper.execution.looper;

import com.dlong.creeper.annotation.control.ExecutionMode;
import com.dlong.creeper.control.BreakAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.base.*;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.handler.RecorderLoopExecutionResultHandler;
import com.dlong.creeper.execution.registry.base.LoopExecutionResultHandlerRegistry;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.multi.Multiple;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.control.Looper;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;

public abstract class BaseExecuteLooper<T extends LoopableEntity> implements ExecuteLooper<T>, Cloneable, ParallelThreadHandleControl {
    private static Logger logger = Logger.getLogger(ForIndexExecuteLooper.class);
    private LoopExecutionResultHandlerRegistry loopExecutionResultHandlerRegistry;
    protected LoopableExecutor<T> executor;

    private Class looperClass;

    public BaseExecuteLooper(LoopableExecutor<T> executor, Class looperClass) {
        this.executor = executor;
        this.looperClass = looperClass;
        this.loopExecutionResultHandlerRegistry = new LoopExecutionResultHandlerRegistry();
        this.loopExecutionResultHandlerRegistry.registerExecutionHandler(new RecorderLoopExecutionResultHandler());
    }

    @Override
    public BaseExecuteLooper clone() throws CloneNotSupportedException {
        BaseExecuteLooper executeLooper = (BaseExecuteLooper) super.clone();
        if (!(executor instanceof AbstractLoopableExecutor)) {
            throw new RuntimeExecuteException("Execution not support for executor " + executor);
        }

        AbstractLoopableExecutor abstractLoopableExecutor = (AbstractLoopableExecutor) this.executor;
        LoopableExecutor clonedExecutor = abstractLoopableExecutor.clone();
        executeLooper.setExecutor(clonedExecutor);
        return executeLooper;
    }

    /**
     * 1.提前获取下一Seq，并放入Loop执行结果中
     * 2.顺序执行：循环中根据执行结果，更新Loop的执行结果，最后返回Loop执行结果
     * 3.并行执行：为循环执行创建一个线程来执行，然后预先返回并执行下一个Seq
     */
    @Override
    public LoopExecutionResult<T> loop(T loopableEntity) throws ExecutionException, IOException {
        Looper looper = loopableEntity.getLooper();
        checkLooper(looper);

        LoopExecutionResult<T> result = new LoopExecutionResult<>(loopableEntity);
        ChainContext context = getContext();
        this.loopExecutionResultHandlerRegistry.invokeBeforeExecutionHandler(result, context);

        if (result.isSkipExecute()) {
            logger.info("*************** loop " + loopableEntity + " has been skiped ***************");
            return result;
        }

        ExecutionMode executionMode = looper.getExecutionMode();

        if (executionMode == ExecutionMode.PARALLEL && !isIgnoreParallelThreadHandle()) {
            result = parallelLoop(loopableEntity);
        } else {
            logger.info("*************** begin loop " + loopableEntity + " ***************");
            result = doLoop(loopableEntity);
            logger.info("*************** end loop " + loopableEntity + " ***************\n");
        }

        this.loopExecutionResultHandlerRegistry.invokeAfterExecutionHandler(result, context);

        if (result.isFailed()) {
            logger.error("循环执行失败");
            return result;
        }
        return result;
    }

    private LoopExecutionResult<T> parallelLoop(T loopableEntity) {
        logger.info("*************** begin parallel loop " + loopableEntity + " ***************");
        LoopExecutionResult<T> result = new LoopExecutionResult<>(loopableEntity);
        BaseExecuteLooper clonedLooper = null;
        try {
            clonedLooper = this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        loopInThread(loopableEntity, clonedLooper);
        //TODO something not right
        result.setNextSeq(getContext().getSequntialFinder().findNextSeq(loopableEntity));

        return result;
    }

    private void loopInThread(T loopableEntity, BaseExecuteLooper clonedLooper) {
        Thread thread = new Thread(() -> {
            try {
                LoopExecutionResult parallelResult = clonedLooper.doLoop(loopableEntity);
                if (parallelResult.isFailed()) {
                    logger.error("循环执行失败");
                }
                logger.info("*************** end parallel loop " + loopableEntity + " ***************\n");
            } catch (ExecutionException | IOException e) {
                e.printStackTrace();
            }
        }, "P-" + Thread.currentThread().getName());
        thread.start();

        //存储产生的ParallelThread，ParallelThread会替换当前父线程
        loopableEntity.setParallelThread(thread);
    }

    public static boolean isBreak(ExecutionResult innerResult) {
        if (innerResult.isFailed()) {
            return true;
        }
        boolean isBreak = false;
        if (innerResult.getActionResult() != null && BreakAction.class.equals(innerResult.getActionResult().getClass())) {
            isBreak = true;
        } else if (innerResult.getNextSeq() != null && !innerResult.getNextSeq().equals(innerResult.getOrginalSeq())) {
            isBreak = true;
        }
        return isBreak;
    }

    public static boolean isMultipleShutdown(Multiple multiple) {
        return multiple != null && multiple.isShutdown();
    }

    private void checkLooper(Looper looper) {
        Assert.isInstanceOf(looperClass, looper, "is not a suitable Looper for ExecuteLooper please try " + looperClass.getSimpleName());
    }

    public ChainContext getContext() {
        Assert.isInstanceOf(BaseChainContextExecutor.class, executor);
        BaseChainContextExecutor contextExecutor = (BaseChainContextExecutor) this.executor;
        return contextExecutor.getContext();
    }

    public void setExecutor(LoopableExecutor<T> executor) {
        this.executor = executor;
    }

    public abstract LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException, IOException;

    @Override
    public boolean isIgnoreParallelThreadHandle() {
        return false;
    }
}

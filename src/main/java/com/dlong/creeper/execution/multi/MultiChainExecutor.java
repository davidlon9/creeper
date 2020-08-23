package com.dlong.creeper.execution.multi;

import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.base.BaseChainExecutor;
import com.dlong.creeper.execution.base.ChainExecutor;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.context.ExecutionContext;
import com.dlong.creeper.execution.resolver.MultiChainExecutionResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.MultiChainExecutionResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.multi.MultiRequestChainEntity;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiChainExecutor extends BaseChainExecutor<MultiRequestChainEntity> {
    private static Logger logger=Logger.getLogger(MultiChainExecutor.class);

    private MultiChainExecutionResultResolver multiResultResolver;

    public MultiChainExecutor(ExecutionContext context) {
        super(context,true);
        multiResultResolver=new MultiChainExecutionResultResolver();
    }

    @Override
    public MultiChainExecutionResult<MultiRequestChainEntity> execute() {
        RequestChainEntity rootChain = getRootChain();
        if(rootChain instanceof MultiRequestChainEntity){
            return this.execute((MultiRequestChainEntity) rootChain);
        }else{
            throw new RuntimeExecuteException("root chain is not suit type for excutor please try "+MultiRequestChainEntity.class.getSimpleName());
        }
    }

    @Override
    public MultiChainExecutionResult<MultiRequestChainEntity> execute(MultiRequestChainEntity multiChainEntity) {
        logger.info("MultiChain "+ multiChainEntity+" 开始执行");

        int threadSize = multiChainEntity.getThreadSize();
        MultiChainExecutionResult<MultiRequestChainEntity> executionResult=new MultiChainExecutionResult<>(multiChainEntity);

        MultiEntityThreadFactory threadFactory=new MultiEntityThreadFactory(Thread.currentThread().getName(),multiChainEntity.getName());
        ExecutorService threadPool = Executors.newFixedThreadPool(threadSize,threadFactory);
        multiChainEntity.setLocalThreadPool(threadPool);

        long s=System.currentTimeMillis();
        execute(threadSize,threadPool,multiChainEntity,executionResult);

        if(executionResult.isFailed()){
            logger.error("MultiChain "+ multiChainEntity+" 执行失败");
        }else if(executionResult.isOtherThreadSuccessed()){
            logger.warn("MultiChain "+ multiChainEntity+" 执行失败，因为其他线程已经成功");
        }else{
            //TODO 抛出Catch
            try {
                multiResultResolver.afterExecuteResovle(executionResult,super.getContext());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(executionResult.getNextSeq()==null){
                logger.warn("successed chain don't have next");
            }
        }

        logger.info("MultiChain finished!!! Use Time-"+(System.currentTimeMillis()-s)+"\n");
        return executionResult;
    }

    private void execute(int threadSize, ExecutorService threadPool, MultiRequestChainEntity multiChainEntity, MultiChainExecutionResult<MultiRequestChainEntity> executionResult) {
        try {
            CountDownLatch joinMainLatch=new CountDownLatch(threadSize);
            //启动threadSize个线程
            for (int i = 0; i < threadSize; i++) {
                threadPool.execute(new ExecuteMultiChain(multiChainEntity,executionResult,joinMainLatch));
            }
            //等待线程执行
            joinMainLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if(!threadPool.isShutdown()){
                threadPool.shutdown();
            }
        }
    }

    private ExecutionResult<MultiRequestChainEntity> superExecute(MultiRequestChainEntity multiChainEntity) throws IOException, ExecutionException {
        return super.execute(multiChainEntity);
    }

    class ExecuteMultiChain implements Runnable{
        private MultiRequestChainEntity multiChainEntity;
        private MultiChainExecutionResult<MultiRequestChainEntity> executionResult;
        //加入主线程的Latch
        private CountDownLatch joinMainLatch;

        public ExecuteMultiChain(MultiRequestChainEntity multiChainEntity, MultiChainExecutionResult<MultiRequestChainEntity> executionResult, CountDownLatch joinMainLatch) {
            this.multiChainEntity = multiChainEntity;
            this.executionResult = executionResult;
            this.joinMainLatch = joinMainLatch;
        }

        @Override
        public void run() {
            ExecutionResult<MultiRequestChainEntity> result=null;
            try {
                ExecutionContext context = getContext();
                ContextParamStore contextStore = context.getContextStore();
                logger.info(Thread.currentThread().getId()+" start time "+System.currentTimeMillis());
                ChainExecutor<MultiRequestChainEntity> executor = new BaseChainExecutor<MultiRequestChainEntity>(context);
                result = executor.execute(multiChainEntity);
                //如果moveStopAll为false，那么则以最后一个线程的nextSeq作为下一Seq,
            }catch (RequestAbortedException e){
                //其他线程执行成功，导致该线程interrupted，同时该线程正在执行Request，就会报RequestAbortedException
                logger.warn(Thread.currentThread().getName()+" is interrupted and request aborted because of other thread is successed!");
            }catch (IOException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                if(result==null){
                    //如果其他线程在该线程执行前就打断了它，则result为null，需要为result初始化
                    result=new ExecutionResult<>(multiChainEntity);
                }
                executionResult.addThreadResult(result);
                //防止某线程执行sleep时，被其他线程打断导致无法继续程序
                joinMainLatch.countDown();
            }
        }

    }
}

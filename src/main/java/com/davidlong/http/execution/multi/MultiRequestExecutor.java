package com.davidlong.http.execution.multi;

import com.davidlong.http.exception.ExecutionException;
import com.davidlong.http.execution.base.BaseRequestExecutor;
import com.davidlong.http.execution.base.RequestExecutor;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.resolver.MultiExecutionResultResolver;
import com.davidlong.http.model.ExecutionResult;
import com.davidlong.http.model.MultiExecutionResult;
import com.davidlong.http.model.seq.multi.MultiRequestEntity;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiRequestExecutor extends BaseRequestExecutor<MultiRequestEntity> {
    private static Logger logger=Logger.getLogger(MultiRequestExecutor.class);

    private MultiExecutionResultResolver multiResultResolver;

    public MultiRequestExecutor(ExecutionContext context) {
        super(context,true);
        this.multiResultResolver=new MultiExecutionResultResolver();
//        super.setResultResolver(new MultiExecutionResultResolver());
//        super.getHandlerExecuteHandlerRegistry().registerExecutionHandler(new MultiResultRequestHandler<>());
    }

    @Override
    public ExecutionResult<MultiRequestEntity> execute(MultiRequestEntity multiRequestEntity) {
        int threadSize = multiRequestEntity.getThreadSize();
        MultiExecutionResult<MultiRequestEntity> executionResult=new MultiExecutionResult<>(multiRequestEntity);

        MultiEntityThreadFactory threadFactory=new MultiEntityThreadFactory(Thread.currentThread().getName(),multiRequestEntity.getName());
        ExecutorService threadPool = Executors.newFixedThreadPool(threadSize,threadFactory);
        multiRequestEntity.setLocalThreadPool(threadPool);

        long s=System.currentTimeMillis();
        execute(threadSize,threadPool,multiRequestEntity,executionResult);

        if(isFailed(executionResult)){
            logger.error("MultiRequest "+ multiRequestEntity+" 执行失败");
        }else if(executionResult.isOtherThreadSuccessed()){
            logger.warn("MultiRequest "+ multiRequestEntity+" 执行失败，因为其他线程已经成功");
        }else{
            try {
                multiResultResolver.afterExecuteResovle(executionResult,super.getContext());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(executionResult.getNextSeq()==null){
                logger.warn("successed request don't have next");
            }
        }

        logger.info("MultiRequest finished!!! Use Time-"+(System.currentTimeMillis()-s)+"\n");
        return executionResult;
    }

    private void execute(int threadSize,ExecutorService threadPool,MultiRequestEntity multiRequestEntity,MultiExecutionResult<MultiRequestEntity> executionResult) {
        try {
            CountDownLatch joinMainLatch=new CountDownLatch(threadSize);
            //启动threadSize个线程
            for (int i = 0; i < threadSize; i++) {
                threadPool.execute(new MultiRequestExecute(multiRequestEntity,executionResult,joinMainLatch));
            }
            //等待线程执行
            joinMainLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            if(!threadPool.isShutdown()){
                threadPool.shutdown();
            }
        }
    }

    public boolean isFailed(MultiExecutionResult<MultiRequestEntity> result){
        List<ExecutionResult> threadResults = result.getThreadResults();
        for (ExecutionResult threadResult : threadResults) {
            if(threadResult!=null && threadResult.isFailed()){
                return true;
            }
        }
        return false;
    }

    private ExecutionResult<MultiRequestEntity> superExecute(MultiRequestEntity multiRequestEntity) throws IOException, ExecutionException {
        return super.execute(multiRequestEntity);
    }

    class MultiRequestExecute implements Runnable{
        private MultiRequestEntity multiRequestEntity;
        private MultiExecutionResult<MultiRequestEntity> executionResult;
        //加入主线程的Latch
        private CountDownLatch joinMainLatch;

        public MultiRequestExecute(MultiRequestEntity multiRequestEntity, MultiExecutionResult<MultiRequestEntity> executionResult, CountDownLatch joinMainLatch) {
            this.multiRequestEntity = multiRequestEntity;
            this.executionResult = executionResult;
            this.joinMainLatch = joinMainLatch;
        }

        @Override
        public void run() {
            ExecutionResult<MultiRequestEntity> result=null;
            try {
                logger.info(Thread.currentThread().getName()+" start time "+System.currentTimeMillis());
                RequestExecutor<MultiRequestEntity> innerExecutor = new BaseRequestExecutor<MultiRequestEntity>(getContext());
                result = innerExecutor.execute(multiRequestEntity);
                //如果moveStopAll为false，那么则以最后一个线程的nextSeq作为下一Seq,
            }catch (RequestAbortedException e){
                //其他线程执行成功，导致该线程interrupted，同时该线程正在执行Request，就会报RequestAbortedException
                logger.warn(Thread.currentThread().getName()+" is interrupted and request aborted because of other thread is successed!");
            }catch (IOException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                if(result==null){
                    //如果其他线程在该线程执行前就打断了它，则result为null，需要为result初始化
                    result=new ExecutionResult<>(multiRequestEntity);
                }
                executionResult.addThreadResult(result);
                //防止某线程执行sleep时，被其他线程打断导致无法继续程序
                joinMainLatch.countDown();
            }
        }

    }
}

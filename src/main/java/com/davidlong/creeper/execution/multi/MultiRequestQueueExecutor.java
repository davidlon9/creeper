package com.davidlong.creeper.execution.multi;

import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.exception.RuntimeExecuteException;
import com.davidlong.creeper.execution.base.BaseRequestExecutor;
import com.davidlong.creeper.execution.base.RequestExecutor;
import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.looper.BaseExecuteLooper;
import com.davidlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.davidlong.creeper.execution.resolver.MultiExecutionResultResolver;
import com.davidlong.creeper.model.result.ExecutionResult;
import com.davidlong.creeper.model.result.LoopExecutionResult;
import com.davidlong.creeper.model.result.MultiExecutionResult;
import com.davidlong.creeper.model.seq.multi.MultiRequestQueueEntity;
import com.davidlong.creeper.model.seq.multi.Multiple;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class MultiRequestQueueExecutor extends BaseRequestExecutor<MultiRequestQueueEntity> {
    private static Logger logger=Logger.getLogger(MultiRequestQueueExecutor.class);

    private MultiExecutionResultResolver multiResultResolver;

    public MultiRequestQueueExecutor(ExecutionContext context) {
        super(context,true);
        this.multiResultResolver=new MultiExecutionResultResolver();
    }

    @Override
    public ExecutionResult<MultiRequestQueueEntity> execute(MultiRequestQueueEntity multiQueueEntity){
        int threadSize = multiQueueEntity.getThreadSize();
        MultiExecutionResult<MultiRequestQueueEntity> executionResult=new MultiExecutionResult<>(multiQueueEntity);

        String queueContextKey = multiQueueEntity.getQueueContextKey();
        //main线程的context
        ExecutionContext mainContext = getContext();
        ContextParamStore contextStore = mainContext.getContextStore();

        BlockingQueue<Object> queue = getQueue(queueContextKey, contextStore);

        MultiEntityThreadFactory threadFactory=new MultiEntityThreadFactory(Thread.currentThread().getName(),multiQueueEntity.getName());
        ExecutorService threadPool = Executors.newFixedThreadPool(threadSize,threadFactory);
        multiQueueEntity.setLocalThreadPool(threadPool);

        //启动threadSize个线程
        for (int i = 0; i < threadSize; i++) {
            threadPool.execute(new MultiRequestQueueExecute(multiQueueEntity,executionResult,threadPool,queue,mainContext));
        }

        try {
            new AutoNextSeqResultResolver().afterExecuteResovle(executionResult, mainContext);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        logger.info("MultiRequestQueue "+multiQueueEntity+" is running!!!");
        return executionResult;
    }

    private BlockingQueue<Object> getQueue(String queueContextKey, ContextParamStore contextStore) {
        Object value = contextStore.getValue(queueContextKey);
        BlockingQueue<Object> queue;
        if(value!=null){
            if(value instanceof BlockingQueue){
                queue = (BlockingQueue<Object>) value;
            }else if(value instanceof Collection){
                queue = new LinkedBlockingQueue<>((Collection)value);
            }else{
                throw new RuntimeExecuteException("queue context key:"+queueContextKey+" maped object type "+value.getClass()+" is not support.please try these interface [BlockingQueue,Collection]");
            }
        }else{
            queue=new LinkedBlockingQueue<>();
            //main线程的context
            contextStore.addParam(queueContextKey,queue);
        }
        return queue;
    }

    public boolean isFailed(MultiExecutionResult<MultiRequestQueueEntity> result){
        List<ExecutionResult> threadResults = result.getThreadResults();
        for (ExecutionResult threadResult : threadResults) {
            if(threadResult!=null && threadResult.isFailed()){
                return true;
            }
        }
        return false;
    }

    private ExecutionResult<MultiRequestQueueEntity> superExecute(MultiRequestQueueEntity multiRequestEntity) throws IOException, ExecutionException {
        return super.execute(multiRequestEntity);
    }

    class MultiRequestQueueExecute implements Runnable{
        private MultiRequestQueueEntity multiRequestEntity;
        private MultiExecutionResult<MultiRequestQueueEntity> executionResult;
        //加入主线程的Latch
        private ExecutorService threadPool;
        private final BlockingQueue<Object> queue;
        private ExecutionContext mainContext;
        public MultiRequestQueueExecute(MultiRequestQueueEntity multiRequestEntity, MultiExecutionResult<MultiRequestQueueEntity> executionResult,ExecutorService threadPool, BlockingQueue<Object> queue,ExecutionContext mainContext) {
            this.multiRequestEntity = multiRequestEntity;
            this.executionResult = executionResult;
            this.threadPool = threadPool;
            this.queue = queue;
            this.mainContext = mainContext;
        }

        @Override
        public void run() {
            logger.info(Thread.currentThread().getName()+" start time "+System.currentTimeMillis());
            LoopExecutionResult<MultiRequestQueueEntity> result=new LoopExecutionResult<>(multiRequestEntity);
            try {
                RequestExecutor<MultiRequestQueueEntity> innerExecutor = null;
                Boolean condition=true;
                Thread.sleep(multiRequestEntity.getDelay());
                do {
                    if(queue.size()==0){
                        continue;
                    }

                    if(isMultipleShutdown(multiRequestEntity)){
                        result.setOtherThreadSuccessed(true);
                        logger.warn("Queue of "+multiRequestEntity+" is break probably cause by other thread successed!");
                        break;
                    }
                    Object poll = queue.poll();
                    if(poll==null){
                        continue;
                    }
                    //thread context
                    ExecutionContext context = getContext();
                    ContextParamStore contextStore = context.getContextStore();
                    String queueElementKey = multiRequestEntity.getQueueElementKey();
                    contextStore.addParam(queueElementKey,poll);
                    logger.info("# Queue element "+queueElementKey+" of "+multiRequestEntity+" will be execute");

                    if(innerExecutor==null){
                        innerExecutor = new BaseRequestExecutor<>(context);
                    }
                    ExecutionResult<MultiRequestQueueEntity> innerResult = innerExecutor.execute(multiRequestEntity);
                    result.addLoopResult(innerResult);

                   if(BaseExecuteLooper.isBreak(innerResult)){
                       result.setNextSeq(innerResult.getNextSeq());
                       break;
                   }
                    condition = mainContext.getExpressionParser().parse(multiRequestEntity.getStopConditionExpr(), Boolean.class);
                   if(condition && queue.size()==0){
                       logger.warn("no element in queue ["+ queueElementKey+"]");
                       break;
                   }
                }while (queue.size()>0);
            }catch (RequestAbortedException e){
                //其他线程执行成功，导致该线程interrupted，同时该线程正在执行Request，就会报RequestAbortedException
                logger.warn(Thread.currentThread().getName()+" is interrupted and request aborted because of other thread is successed!");
            }catch (IOException | ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if(result==null){
                    //如果其他线程在该线程执行前就打断了它，则result为null，需要为result初始化
                    result=new LoopExecutionResult<>(multiRequestEntity);
                }
                executionResult.addThreadResult(result);
                //防止某线程执行sleep时，被其他线程打断导致无法继续程序
                if(!threadPool.isShutdown()){
                    threadPool.shutdown();
                }
            }
            if(isFailed(executionResult)){
                logger.error("MultiRequestQueue "+ multiRequestEntity+" 执行失败");
            }else if(executionResult.isOtherThreadSuccessed()){
                logger.warn("MultiRequestQueue "+ multiRequestEntity+" 执行失败，因为其他线程已经成功");
            }
            logger.info("MultiRequestQueue "+ multiRequestEntity+" finished!!!");
        }

    }

    public static boolean isMultipleShutdown(Multiple multiple) {
        return multiple!=null && multiple.isShutdown();
    }
}

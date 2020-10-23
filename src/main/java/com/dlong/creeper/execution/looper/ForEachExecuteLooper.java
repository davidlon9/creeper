package com.dlong.creeper.execution.looper;

import com.dlong.creeper.control.RetryAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.base.LoopableExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.dlong.creeper.execution.spliter.SpliterHandler;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.control.ForEachLooper;
import com.dlong.creeper.model.seq.control.Spliter;
import com.dlong.creeper.model.seq.multi.Multiple;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ForEachExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger= Logger.getLogger(ForEachExecuteLooper.class);

    public ForEachExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ForEachLooper.class);
    }

    @Override
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException,IOException {
        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);
        ContextParamStore contextStore = getContext().getContextStore();
        ForEachLooper forEachLooper = (ForEachLooper) loopableEntity.getLooper();
        Collection items;
        if(forEachLooper.isPredicted()){
            items = forEachLooper.getPredictItems();
        }else{
            items = getItems(contextStore, forEachLooper.getItemsContextKey());
        }
        Spliter spliter = forEachLooper.getSpliter();
        if(spliter!=null){
            doSplitIterate(loopResult, contextStore, forEachLooper, items, spliter);
        }else{
            doIterate(loopResult, contextStore, forEachLooper.getItemName(), new AtomicInteger(1), items);
        }
        new AutoNextSeqResultResolver().afterExecuteResovle(loopResult,getContext());
        return loopResult;
    }

    private void doSplitIterate(LoopExecutionResult<T> loopResult, ContextParamStore contextStore, ForEachLooper forEachLooper, Collection items, Spliter spliter) throws ExecutionException {
        AtomicInteger count = new AtomicInteger(1);
        Integer splitSize = spliter.getSplitSize();
        List<List<Object>> lists = splitList(new ArrayList<>(items), splitSize);
        CountDownLatch countDownLatch=new CountDownLatch(lists.size());//等待当前500执行完毕
        ExecutorService threadPool = Executors.newFixedThreadPool(lists.size());
        SpliterHandler handler = spliter.getHandler();
        for (int i = 0; i < lists.size(); i++) {
            List<Object> list = lists.get(i);
            Integer idx = i;
            //异步并行遍历分裂的数据
            threadPool.execute(()->{
                handler.beforeSplitExecuteHandle(lists,idx);
                Exception exception=null;
                try {
                    doIterate(loopResult, contextStore, forEachLooper.getItemName(), count, list);
                } catch (IOException | ExecutionException e) {
                    logger.error(e.getMessage());
                    exception = e;
                    int retryCount = spliter.getMaxRetryNum();
                    while (retryCount>0){
                        try {
                            doIterate(loopResult, contextStore, forEachLooper.getItemName(), count, list);
                            break;
                        } catch (IOException | ExecutionException e1) {
                            logger.error(e.getMessage());
                            exception = e1;
                            retryCount--;
                        }
                    }
                }
                handler.afterSplitExecuteHandle(lists,idx,exception);
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
            logger.info("split loop finished");
        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public void doPredict(LoopExecutionResult<T> result, ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = (RequestEntity) result.getOrginalSeq();
        ForEachLooper forEachLooper = (ForEachLooper) requestEntity.getLooper();
        ContextParamStore contextStore = context.getContextStore();
        Collection items = getItems(contextStore, forEachLooper.getItemsContextKey());
        for (Object obj : items) {
            contextStore.addParam(forEachLooper.getItemName(),obj);
            forEachLooper.putPredictUrlItem(requestEntity.buildUrl(context),obj);
        }
    }

    private Collection getItems(ContextParamStore contextStore, String itemsContextKey) throws ExecutionException {
        Object value = contextStore.getValue(itemsContextKey);
        if(value instanceof Collection){
            return (Collection) value;
        }else if(value == null){
            throw new ExecutionException("itemsContextKey "+ itemsContextKey +" mapped paramValue is null");
        }else{
            throw new ExecutionException("itemsContextKey "+itemsContextKey+" mapped paramValue is not a Collection");
        }
    }

    private void doIterate(LoopExecutionResult<T> loopResult, ContextParamStore contextStore, String itemName, AtomicInteger count, Collection collection) throws IOException, ExecutionException {
        T loopableEntity = loopResult.getOrginalSeq();
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;
        for (Object obj : collection) {
            if(isMultipleShutdown(multiple)){
                loopResult.setOtherThreadSuccessed(true);
                logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                break;
            }

            contextStore.addParam(itemName,obj);

            logger.info("* Loop "+count+" of "+collection.size()+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());
            ExecutionResult<T> innerResult = executor.doExecute(loopableEntity);
            if(isBreak(innerResult)){
                break;
            }
            if(innerResult.getActionResult() instanceof RetryAction){
                logger.info("* Loop "+count+" of "+collection.size()+" of "+loopableEntity+" will be retry");
                innerResult = retryInLoop(loopableEntity, loopResult);
                if(isBreak(innerResult)){
                    break;
                }
            }else{
                count.getAndAdd(1);
            }
            loopResult.setNextSeq(innerResult.getNextSeq());
            loopResult.addLoopResult(innerResult);
        }
        loopResult.setLoopNum(count.get());
        loopResult.setTotalNum(collection.size());
    }

    private ExecutionResult<T> retryInLoop(T loopableEntity, LoopExecutionResult<T> loopResult) throws IOException, ExecutionException {
        ExecutionResult<T> retryResult = executor.doExecute(loopableEntity);
        if(retryResult.getActionResult() instanceof RetryAction){
            logger.info("* Loop of "+loopableEntity+" will be retry again");
            return retryInLoop(loopableEntity, loopResult);
        }
        return retryResult;
    }

}

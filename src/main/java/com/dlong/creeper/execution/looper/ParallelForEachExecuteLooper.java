package com.dlong.creeper.execution.looper;

import com.dlong.creeper.control.RetryAction;
import com.dlong.creeper.exception.ExecutionException;
import com.dlong.creeper.execution.base.LoopableExecutor;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.context.ContextParamStore;
import com.dlong.creeper.execution.resolver.AutoNextSeqResultResolver;
import com.dlong.creeper.model.result.ExecutionResult;
import com.dlong.creeper.model.result.LoopExecutionResult;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.control.Looper;
import com.dlong.creeper.model.seq.control.ParallelForEachLooper;
import com.dlong.creeper.model.seq.multi.Multiple;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelForEachExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger= Logger.getLogger(ParallelForEachExecuteLooper.class);

    public ParallelForEachExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ParallelForEachLooper.class);
    }

    @Override
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException,IOException {
        ChainContext chainContext = getContext();
        ContextParamStore contextStore = chainContext.getContextStore();

        Looper looper = loopableEntity.getLooper();
        ParallelForEachLooper forEachLooper = (ParallelForEachLooper) looper;
        Multiple multiple = loopableEntity instanceof Multiple ? (Multiple) loopableEntity:null;
        String itemsContextKey = forEachLooper.getItemsContextKey();
        Collection items;
        if(forEachLooper.isPredicted()){
            items = forEachLooper.getPredictItems();
        }else{
            items = getItems(contextStore,itemsContextKey);
        }
        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);
        AtomicInteger count=new AtomicInteger(0);
        AtomicBoolean isStop=new AtomicBoolean(false);
        AtomicReference<Exception> exception=new AtomicReference<>(null);

        ForkJoinPool forkJoinPool = new ForkJoinPool(forEachLooper.getParallelism());
        try {
            forkJoinPool.submit(() -> doIterate(loopableEntity, contextStore, forEachLooper, multiple, items, loopResult, count, isStop, exception)).get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            exception.set(e);
        }
        Exception e = exception.get();
        if(e !=null){
            throw new ExecutionException(e);
        }
        loopResult.setLoopNum(count.get());
        loopResult.setTotalNum(items.size());
        new AutoNextSeqResultResolver().afterExecuteResovle(loopResult,getContext());
        return loopResult;
    }

    private void doIterate(T loopableEntity, ContextParamStore contextStore, ParallelForEachLooper forEachLooper, Multiple multiple, Collection items, LoopExecutionResult<T> loopResult, AtomicInteger count, AtomicBoolean isStop, AtomicReference<Exception> exception) {
        items.parallelStream().forEach(obj->{
            if(isStop.get()){
                logger.info("Parallel Loop is breaked "+obj+" won't be iterate");
                return;
            }
            if(isMultipleShutdown(multiple)){
                loopResult.setOtherThreadSuccessed(true);
                logger.warn("Looper of "+loopableEntity+" is break probably cause by other thread successed!");
                isStop.set(true);
                return;
            }

            contextStore.addParam(forEachLooper.getItemName(),obj);
            logger.info("* Parallel Loop "+count.addAndGet(1)+" of "+items.size()+" of "+loopableEntity+" will be execute by "+this.getClass().getSimpleName());
            ExecutionResult<T> innerResult;
            try {
                innerResult = executor.doExecute(loopableEntity);
                if(isBreak(innerResult)){
                    isStop.set(true);
                    return;
                }
                if(innerResult.getActionResult() instanceof RetryAction){
                    logger.info("* Parallel Loop "+count.get()+" of "+items.size()+" of "+loopableEntity+" will be retry");
                    innerResult = retryInLoop(loopableEntity, loopResult);
                    if(isBreak(innerResult)){
                        isStop.set(true);
                        return;
                    }
                }
            } catch (IOException | ExecutionException e) {
                exception.set(e);
                isStop.set(true);
                return;
            }
            loopResult.setNextSeq(innerResult.getNextSeq());
            loopResult.addLoopResult(innerResult);
        });
    }

    @Override
    public void doPredict(LoopExecutionResult<T> result, ChainContext context) throws ExecutionException {
        RequestEntity requestEntity = (RequestEntity) result.getOrginalSeq();
        ParallelForEachLooper forEachLooper = (ParallelForEachLooper) requestEntity.getLooper();
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

    private ExecutionResult<T> retryInLoop(T loopableEntity, LoopExecutionResult<T> loopResult) throws IOException, ExecutionException {
        ExecutionResult<T> retryResult = executor.doExecute(loopableEntity);
        if(retryResult.getActionResult() instanceof RetryAction){
            logger.info("* Loop of "+loopableEntity+" will be retry again");
            return retryInLoop(loopableEntity, loopResult);
        }
        return retryResult;
    }

    public static void main(String[] args) throws java.util.concurrent.ExecutionException, InterruptedException {
        List<Integer> integerList= IntStream.range(1,500).boxed().collect(Collectors.toList());
        System.out.println();
        AtomicInteger count=new AtomicInteger(0);
        ForkJoinPool customThreadPool = new ForkJoinPool(8);
        List<Integer> nums=new ArrayList<>();
        ForkJoinTask<?> submit = customThreadPool.submit(() -> {
            integerList.parallelStream().forEach((i) -> {
                System.out.println(Thread.currentThread().getName() + ":" + i + "  count:" + count.addAndGet(1));
                nums.add(i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        submit.get();
        Collections.sort(nums);
        System.out.println(nums.size());
    }
}

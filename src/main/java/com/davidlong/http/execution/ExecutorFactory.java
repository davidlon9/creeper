package com.davidlong.http.execution;

import com.davidlong.http.exception.RuntimeExecuteException;
import com.davidlong.http.execution.base.*;
import com.davidlong.http.execution.context.ExecutionContext;
import com.davidlong.http.execution.looper.*;
import com.davidlong.http.execution.multi.MultiChainExecutor;
import com.davidlong.http.execution.multi.MultiRequestExecutor;
import com.davidlong.http.execution.multi.MultiRequestQueueExecutor;
import com.davidlong.http.execution.multi.MultiUserExecutor;
import com.davidlong.http.model.seq.LoopableEntity;
import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.model.seq.RequestEntity;
import com.davidlong.http.model.seq.SeqRequestEntity;
import com.davidlong.http.model.seq.control.*;
import com.davidlong.http.model.seq.multi.MultiRequestChainEntity;
import com.davidlong.http.model.seq.multi.MultiRequestEntity;
import com.davidlong.http.model.seq.multi.MultiRequestQueueEntity;
import com.davidlong.http.model.seq.multi.MultiUserChainEntity;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExecutorFactory {
    private static Map<Class<? extends Looper>,Class> looperExecutorClassMap = new HashMap<>();
    private static Map<Class<? extends RequestChainEntity>,Class> chainExecutorClassMap = new HashMap<>();
    private static Map<Class<? extends RequestEntity>,Class> requestExecutorClassMap = new HashMap<>();

    static {
        registerExecutor(RequestChainEntity.class,RequestChainExecutor.class);
        registerExecutor(MultiRequestChainEntity.class,MultiChainExecutor.class);
        registerExecutor(MultiUserChainEntity.class,MultiUserExecutor.class);

        registerExecutor(SeqRequestEntity.class,SeqRequestExecutor.class);
        registerExecutor(MultiRequestEntity.class,MultiRequestExecutor.class);
        registerExecutor(MultiRequestQueueEntity.class,MultiRequestQueueExecutor.class);

        registerExecutor(WhileLooper.class, WhileExecuteLooper.class);
        registerExecutor(ScheduleLooper.class, ScheduleExecuteLooper.class);
        registerExecutor(ForEachLooper.class, ForEachExecuteLooper.class);
        registerExecutor(ForIndexLooper.class, ForIndexExecuteLooper.class);
    }

    @SuppressWarnings("unchecked")
    private static void registerExecutor(Class<?> entityClass, Class<?> executorClass) {
        if (RequestEntity.class.isAssignableFrom(entityClass)) {
            Assert.isAssignable(BaseRequestExecutor.class,executorClass,"executor class "+executorClass+" must extend by BaseRequestExecutor.class");
            requestExecutorClassMap.put((Class<? extends RequestEntity>) entityClass,executorClass);
        }else if(RequestChainEntity.class.isAssignableFrom(entityClass)){
            Assert.isAssignable(BaseChainExecutor.class,executorClass,"executor class "+executorClass+" must extend by BaseChainExecutor.class");
            chainExecutorClassMap.put((Class<? extends RequestChainEntity>) entityClass,executorClass);
        }else if(Looper.class.isAssignableFrom(entityClass)){
            Assert.isAssignable(BaseExecuteLooper.class,executorClass,"executor class "+executorClass+" must extend by BaseExecuteLooper.class");
            looperExecutorClassMap.put((Class<? extends Looper>) entityClass, executorClass);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends RequestEntity> RequestExecutor<T> createRequestExecutor(Class<? extends RequestEntity> requestClass, ExecutionContext context) {
        Set<Class<? extends RequestEntity>> classes = requestExecutorClassMap.keySet();
        if(classes.contains(requestClass)){
            Class<?> executorClz = requestExecutorClassMap.get(requestClass);
            try {
                Constructor constructor = executorClz.getConstructor(ExecutionContext.class);
                constructor.setAccessible(true);
                return (RequestExecutor<T>) constructor.newInstance(context);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeExecuteException(requestClass.getSimpleName()+" is not support");
    }

    public static ChainExecutor createChainExecutor(Class<? extends RequestChainEntity> chainClass, ExecutionContext context) {
        Set<Class<? extends RequestChainEntity>> classes = chainExecutorClassMap.keySet();
        if(classes.contains(chainClass)){
            Class<?> executorClz = chainExecutorClassMap.get(chainClass);
            try {
                Constructor constructor = executorClz.getConstructor(ExecutionContext.class);
                constructor.setAccessible(true);
                return (ChainExecutor) constructor.newInstance(context);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeExecuteException(chainClass.getSimpleName()+" is not support");
    }

    @SuppressWarnings("unchecked")
    public static <T extends LoopableEntity> ExecuteLooper<T> createExecuteLooper(Class<? extends Looper> looperClass, LoopableExecutor<T> executor) {
        Set<Class<? extends Looper>> classes = looperExecutorClassMap.keySet();
        if(classes.contains(looperClass)){
            Class<?> looperClz = looperExecutorClassMap.get(looperClass);
            try {
                Constructor constructor = looperClz.getConstructor(LoopableExecutor.class);
                constructor.setAccessible(true);
                return (ExecuteLooper<T>) constructor.newInstance(executor);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeExecuteException(looperClass.getSimpleName()+" is not support");
    }

    public static void main(String[] args) {
        System.out.println();
    }
}

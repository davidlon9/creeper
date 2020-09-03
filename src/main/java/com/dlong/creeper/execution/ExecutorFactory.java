package com.dlong.creeper.execution;

import com.dlong.creeper.exception.RuntimeExecuteException;
import com.dlong.creeper.execution.base.*;
import com.dlong.creeper.execution.context.ChainContext;
import com.dlong.creeper.execution.looper.*;
import com.dlong.creeper.execution.multi.MultiChainExecutor;
import com.dlong.creeper.execution.multi.MultiRequestExecutor;
import com.dlong.creeper.execution.multi.MultiRequestQueueExecutor;
import com.dlong.creeper.execution.multi.MultiUserExecutor;
import com.dlong.creeper.model.seq.LoopableEntity;
import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.RequestEntity;
import com.dlong.creeper.model.seq.SeqRequestEntity;
import com.dlong.creeper.model.seq.control.*;
import com.dlong.creeper.model.seq.multi.MultiRequestChainEntity;
import com.dlong.creeper.model.seq.multi.MultiRequestEntity;
import com.dlong.creeper.model.seq.multi.MultiRequestQueueEntity;
import com.dlong.creeper.model.seq.multi.MultiUserChainEntity;
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
    public static <T extends RequestEntity> RequestExecutor<T> createRequestExecutor(Class<? extends RequestEntity> requestClass, ChainContext context) {
        Set<Class<? extends RequestEntity>> classes = requestExecutorClassMap.keySet();
        if(classes.contains(requestClass)){
            Class<?> executorClz = requestExecutorClassMap.get(requestClass);
            try {
                Constructor constructor = executorClz.getConstructor(ChainContext.class);
                constructor.setAccessible(true);
                return (RequestExecutor<T>) constructor.newInstance(context);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeExecuteException(requestClass.getSimpleName()+" is not support");
    }

    public static ChainExecutor createChainExecutor(Class<? extends RequestChainEntity> chainClass, ChainContext context) {
        Set<Class<? extends RequestChainEntity>> classes = chainExecutorClassMap.keySet();
        if(classes.contains(chainClass)){
            Class<?> executorClz = chainExecutorClassMap.get(chainClass);
            try {
                Constructor constructor = executorClz.getConstructor(ChainContext.class);
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
}

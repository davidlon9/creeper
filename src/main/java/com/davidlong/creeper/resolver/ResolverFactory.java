package com.davidlong.creeper.resolver;

import com.davidlong.creeper.annotation.control.recorder.DatabaseRecordsIgnore;
import com.davidlong.creeper.annotation.control.recorder.FileRecordsIgnore;
import com.davidlong.creeper.annotation.control.looper.ForEach;
import com.davidlong.creeper.annotation.control.looper.ForIndex;
import com.davidlong.creeper.annotation.control.looper.While;
import com.davidlong.creeper.annotation.control.looper.scheduler.Scheduler;
import com.davidlong.creeper.annotation.seq.RequestChain;
import com.davidlong.creeper.annotation.seq.SeqRequest;
import com.davidlong.creeper.annotation.seq.multi.MultiRequest;
import com.davidlong.creeper.annotation.seq.multi.MultiRequestChain;
import com.davidlong.creeper.annotation.seq.multi.MultiRequestQueue;
import com.davidlong.creeper.annotation.seq.multi.MultiUserChain;
import com.davidlong.creeper.exception.RuntimeResolveException;
import com.davidlong.creeper.resolver.looper.*;
import com.davidlong.creeper.resolver.recorder.DatabaseRecordsIgnoreResolver;
import com.davidlong.creeper.resolver.recorder.FileRecordsIgnoreResolver;
import com.davidlong.creeper.resolver.recorder.RecorderResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResolverFactory {
    public static Map<Class<? extends Annotation>,Class> looperAnnoResolverClassMap = new HashMap<>();
    public static Map<Class<? extends Annotation>,Class> chainAnnoResolverClassMap = new HashMap<>();
    public static Map<Class<? extends Annotation>,Class> requestAnnoResolverClassMap = new HashMap<>();
    public static Map<Class<? extends Annotation>,Class> recoderAnnoResolverClassMap = new HashMap<>();

    static {
        chainAnnoResolverClassMap.put(RequestChain.class,RequestChainResolver.class);
        chainAnnoResolverClassMap.put(MultiRequestChain.class,MultiRequestChainResolver.class);
        chainAnnoResolverClassMap.put(MultiUserChain.class,MultiUserChainResolver.class);

        requestAnnoResolverClassMap.put(SeqRequest.class,SeqRequestResolver.class);
        requestAnnoResolverClassMap.put(MultiRequest.class,MultiRequestResolver.class);
        requestAnnoResolverClassMap.put(MultiRequestQueue.class,MultiRequestQueueResolver.class);

        looperAnnoResolverClassMap.put(ForIndex.class, ForIndexLooperResolver.class);
        looperAnnoResolverClassMap.put(ForEach.class, ForEachLooperResolver.class);
        looperAnnoResolverClassMap.put(While.class, WhileLooperResolver.class);
        looperAnnoResolverClassMap.put(Scheduler.class, ScheduleLooperResolver.class);

        recoderAnnoResolverClassMap.put(FileRecordsIgnore.class, FileRecordsIgnoreResolver.class);
        recoderAnnoResolverClassMap.put(DatabaseRecordsIgnore.class, DatabaseRecordsIgnoreResolver.class);
    }

    public static ChainResolver getChainResolver(Class<?> handleClass){
        Set<Class<? extends Annotation>> annoClzs = chainAnnoResolverClassMap.keySet();
        for (Class<? extends Annotation> annoClz : annoClzs) {
            if(handleClass.isAnnotationPresent(annoClz)){
                return getChainResolver(annoClz,handleClass);
            }
        }
        throw new RuntimeResolveException("class "+handleClass.getSimpleName()+" doesn't annotate with RequestChain Annotation");
    }

    public static ChainResolver getChainResolver(Class<? extends Annotation> annoClass,Class handleClass){
        try {
            Class<?> resolverClass = chainAnnoResolverClassMap.get(annoClass);
            Constructor constructor = resolverClass.getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            return (ChainResolver) constructor.newInstance(handleClass);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RequestResolver getRequestResolver(Class<?> handleClass, AnnotatedElement target){
        Set<Class<? extends Annotation>> annoClzs = requestAnnoResolverClassMap.keySet();
        for (Class<? extends Annotation> annoClz : annoClzs) {
            if(target.isAnnotationPresent(annoClz)){
                return getRequestResolver(annoClz,handleClass,target);
            }
        }
        throw new RuntimeResolveException("entityTarget "+target+" doesn't annotate with Request Annotation");
    }

    public static RequestResolver getRequestResolver(Class<? extends Annotation> annoClass,Class handleClass,AnnotatedElement handler){
        try {
            Class<?> resolverClass = requestAnnoResolverClassMap.get(annoClass);
            Constructor constructor = resolverClass.getDeclaredConstructor(Class.class,AnnotatedElement.class);
            constructor.setAccessible(true);
            return (RequestResolver) constructor.newInstance(handleClass,handler);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LooperResolver getLooperResolver(AnnotatedElement target){
        Set<Class<? extends Annotation>> annoClzs = looperAnnoResolverClassMap.keySet();
        for (Class<? extends Annotation> annoClz : annoClzs) {
            if(target.isAnnotationPresent(annoClz)){
                return getLooperResolver(annoClz,target);
            }
        }
        return null;
    }

    public static LooperResolver getLooperResolver(Class<? extends Annotation> annoClass,AnnotatedElement target){
        try {
            Class<?> resolverClass = looperAnnoResolverClassMap.get(annoClass);
            Constructor constructor = resolverClass.getDeclaredConstructor(AnnotatedElement.class);
            constructor.setAccessible(true);
            return (LooperResolver) constructor.newInstance(target);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RecorderResolver getRecoderResolver(AnnotatedElement target){
        Set<Class<? extends Annotation>> annoClzs = recoderAnnoResolverClassMap.keySet();
        for (Class<? extends Annotation> annoClz : annoClzs) {
            if(target.isAnnotationPresent(annoClz)){
                return getRecoderResolver(annoClz,target);
            }
        }
        return null;
    }

    public static RecorderResolver getRecoderResolver(Class<? extends Annotation> annoClz, AnnotatedElement target) {
        try {
            Class<?> resolverClass = recoderAnnoResolverClassMap.get(annoClz);
            Constructor constructor = resolverClass.getDeclaredConstructor(AnnotatedElement.class);
            constructor.setAccessible(true);
            return (RecorderResolver) constructor.newInstance(target);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}

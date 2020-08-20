package com.davidlong.creeper.resolver.base;

import com.davidlong.creeper.annotation.handler.AfterMethod;
import com.davidlong.creeper.annotation.handler.BeforeMethod;
import com.davidlong.creeper.annotation.handler.ExecutionMethod;
import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.exception.RuntimeResolveException;
import com.davidlong.creeper.execution.handler.entity.*;
import com.davidlong.creeper.model.seq.HandleableEntity;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleableResolver extends SequentialResolver{
    private Logger logger=Logger.getLogger(HandleableResolver.class);

    private Class<?> handleClass;

    public HandleableResolver(Class<?> handleClass, AnnotatedElement target, Class<? extends Annotation> annoClass) {
        super(target,annoClass);
        this.handleClass = handleClass;
    }

    public Class<?> getHandleClass() {
        return handleClass;
    }

    public HandleableEntity resolveHandleable(HandleableEntity handleableEntity) throws AnnotationNotFoundException {
        super.resolveSequential(handleableEntity);
        AnnotatedElement target = getTarget();
        if(target instanceof Class){
            setHandlersForChain(handleableEntity);
        }else{
            setHandlersForSeqRequest(handleableEntity,target);
        }
        return handleableEntity;
    }

    private Object getHandlerInstance(Field field){
        Object instance = BaseChainResolver.newChainClassInstance(handleClass);
        field.setAccessible(true);
        Object handler = null;
        try {
            handler = field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Assert.isInstanceOf(SequentialHandler.class,handler);
        return handler;
    }

    private void setHandlersForChainByClass(HandleableEntity handleableEntity){
        try {
            if(ChainExecutionHandler.class.isAssignableFrom(this.handleClass)){
                handleableEntity.setExecutionHandler(this.handleClass.newInstance());
                return;
            }
            List<Class> interfaces = Arrays.asList(this.handleClass.getInterfaces());
            if (interfaces.contains(ChainAfterHandler.class)){
                handleableEntity.setAfterHandler(this.handleClass.newInstance());
            }
            if(interfaces.contains(ChainBeforeHandler.class)){
                handleableEntity.setBeforeHandler(this.handleClass.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeResolveException(e);
        }
    }

    private void setHandlersForChain(HandleableEntity handleableEntity){
        Map<String,Object> handlerMap=new HashMap<>(2);
        ReflectionUtils.doWithMethods(this.handleClass, m -> {
            BeforeMethod beforeMethod = AnnotationUtils.getAnnotation(m, BeforeMethod.class);
            AfterMethod afterMethod = AnnotationUtils.getAnnotation(m, AfterMethod.class);
            ExecutionMethod executionMethod = AnnotationUtils.getAnnotation(m, ExecutionMethod.class);
            if(beforeMethod !=null){
                if(handleableEntity.getName().equals(beforeMethod.value())){
                    handlerMap.put("before",m);
                }
            }
            if(afterMethod !=null){
                if(handleableEntity.getName().equals(afterMethod.value())){
                    handlerMap.put("after",m);
                }
            }
            if(executionMethod !=null){
                if(handleableEntity.getName().equals(executionMethod.value())){
                    handlerMap.put("execution",m);
                }
            }
        },m -> m.isAnnotationPresent(BeforeMethod.class) || m.isAnnotationPresent(AfterMethod.class) || m.isAnnotationPresent(ExecutionMethod.class));

        ReflectionUtils.doWithFields(this.handleClass, f -> {
            BeforeMethod beforeMethod = AnnotationUtils.getAnnotation(f, BeforeMethod.class);
            AfterMethod afterMethod = AnnotationUtils.getAnnotation(f, AfterMethod.class);
            ExecutionMethod executionMethod = AnnotationUtils.getAnnotation(f, ExecutionMethod.class);
            Object handlerInstance = getHandlerInstance(f);
            if(executionMethod != null){
                if(handleableEntity.getName().equals(executionMethod.value()) && handlerInstance instanceof ChainExecutionHandler){
                    handlerMap.put("execution", handlerInstance);
                }
            }
            if(beforeMethod != null){
                if(handleableEntity.getName().equals(beforeMethod.value()) && handlerInstance instanceof ChainBeforeHandler){
                    handlerMap.put("before", handlerInstance);
                }
            }
            if(afterMethod != null){
                if(handleableEntity.getName().equals(afterMethod.value()) && handlerInstance instanceof ChainAfterHandler){
                    handlerMap.put("after", handlerInstance);
                }
            }
        },f -> f.isAnnotationPresent(BeforeMethod.class) || f.isAnnotationPresent(AfterMethod.class) || f.isAnnotationPresent(ExecutionMethod.class));

        Object beforeHandler = handlerMap.get("before");
        Object afterHandler = handlerMap.get("after");
        Object executionHandler = handlerMap.get("execution");

        handleableEntity.setBeforeHandler(beforeHandler);
        handleableEntity.setAfterHandler(afterHandler);
        handleableEntity.setExecutionHandler(executionHandler);

        setHandlersForChainByClass(handleableEntity);
    }

    private void setHandlersForSeqRequest(HandleableEntity handleableEntity,AnnotatedElement target){
        Map<String,Object> handlerMap=new HashMap<>(2);
        Object beforeHandler  = null;
        Object afterHandler = null;
        Object executionHandler  = null;
        if (target instanceof Method) {
            Method method = (Method) target;
            ReflectionUtils.doWithMethods(this.handleClass, m -> {
                BeforeMethod beforeMethod = AnnotationUtils.getAnnotation(m, BeforeMethod.class);
                AfterMethod afterMethod = AnnotationUtils.getAnnotation(m, AfterMethod.class);
                ExecutionMethod executionMethod = AnnotationUtils.getAnnotation(m, ExecutionMethod.class);
                if(beforeMethod !=null){
                    if(m.equals(method)){
                        handlerMap.put("before",m);
                    }else if(handleableEntity.getName().equals(beforeMethod.value())){
                        handlerMap.put("before",m);
                    }
                }
                if(afterMethod !=null){
                    if(m.equals(method)){
                        handlerMap.put("after",m);
                    }else if(handleableEntity.getName().equals(afterMethod.value())){
                        handlerMap.put("after",m);
                    }
                }
                if(executionMethod !=null){
                    if(m.equals(method)){
                        handlerMap.put("execution",m);
                    }else if(handleableEntity.getName().equals(executionMethod.value())){
                        handlerMap.put("execution",m);
                    }
                }
            },m -> m.isAnnotationPresent(BeforeMethod.class) || m.isAnnotationPresent(AfterMethod.class) || m.isAnnotationPresent(ExecutionMethod.class));
            beforeHandler = handlerMap.get("before");
            executionHandler = handlerMap.get("execution");
            afterHandler = handlerMap.get("after");
            if(beforeHandler==null && afterHandler==null){
                afterHandler = method;
            }
        }else if(target instanceof Field){
            Field field = (Field) target;
            ReflectionUtils.doWithFields(this.handleClass, f -> {
                if(f.equals(field)){
                    Object handlerInstance = getHandlerInstance(f);
                    if(handlerInstance instanceof ExecutionHandler){
                        handlerMap.put("execution", handlerInstance);
                    }else if(handlerInstance instanceof BeforeHandler){
                        handlerMap.put("before", handlerInstance);
                    }else if( handlerInstance instanceof AfterHandler){
                        handlerMap.put("after", handlerInstance);
                    }
                }
            });
            beforeHandler = handlerMap.get("before");
            executionHandler = handlerMap.get("execution");
            afterHandler = handlerMap.get("after");
            if(!(afterHandler!=null || beforeHandler!=null || executionHandler!=null)){
                logger.warn("field type request entity does't have a after handler, please try these type implements, AfterHandler BeforeHandler ExecutionHandler");
            }
        }
        handleableEntity.setBeforeHandler(beforeHandler);
        handleableEntity.setAfterHandler(afterHandler);
        handleableEntity.setExecutionHandler(executionHandler);
    }
}

package com.dlong.creeper.resolver.base;

import com.dlong.creeper.annotation.handler.AfterMethod;
import com.dlong.creeper.annotation.handler.BeforeMethod;
import com.dlong.creeper.annotation.handler.ExecutionMethod;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.exception.RuntimeResolveException;
import com.dlong.creeper.execution.handler.entity.*;
import com.dlong.creeper.model.seq.HandleableEntity;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
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
    private static Logger logger=Logger.getLogger(HandleableResolver.class);

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
        Object instance = BaseChainAnnoResolver.newChainClassInstance(handleClass);
        field.setAccessible(true);
        Object handler = null;
        try {
            handler = field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return handler;
    }

    /**
     * 设置Handler到实现了Handler接口的Chain类
     * @param handleableEntity
     */
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
            if(handlerInstance instanceof ChainExecutionHandler
                    || (executionMethod != null && handleableEntity.getName().equals(executionMethod.value()))){
                if(handlerMap.get("execution")!=null){
                    throw new RuntimeResolveException("chain execution handler duplicated");
                }
                handlerMap.put("execution", handlerInstance);
                return;
            }
            if(handlerInstance instanceof ChainBeforeHandler
                    || (beforeMethod != null && handleableEntity.getName().equals(beforeMethod.value()))){
                if(handlerMap.get("before")!=null){
                    throw new RuntimeResolveException("chain execution handler duplicated");
                }
                handlerMap.put("before", handlerInstance);
                return;
            }
            if(handlerInstance instanceof ChainAfterHandler
                    || (afterMethod != null && handleableEntity.getName().equals(afterMethod.value()))){
                if(handlerMap.get("before")!=null){
                    throw new RuntimeResolveException("chain execution handler duplicated");
                }
                handlerMap.put("before", handlerInstance);
            }
        });

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
            if(!(method.isAnnotationPresent(AfterMethod.class) || method.isAnnotationPresent(BeforeMethod.class))){
                handlerMap.put("after",method);
            }
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
            });
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

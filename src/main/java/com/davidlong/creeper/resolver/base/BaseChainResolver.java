package com.davidlong.creeper.resolver.base;

import com.davidlong.creeper.exception.AnnotationNotFoundException;
import com.davidlong.creeper.exception.ExecutorInitializeException;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public class BaseChainResolver extends LoopableSeqResolver{
    private static Logger logger=Logger.getLogger(BaseChainResolver.class);

    public BaseChainResolver(Class<?> handleClass,Class<? extends Annotation> chainAnnoClass) {
        super(handleClass,handleClass,chainAnnoClass);
    }

    public RequestChainEntity resolveChain(RequestChainEntity requestChainEntity) throws AnnotationNotFoundException {
        try {
            super.resolveLoopable(requestChainEntity);
            requestChainEntity.setChainClass(getHandleClass());
            requestChainEntity.setChainInstance(newChainClassInstance(requestChainEntity.getChainClass()));
            return requestChainEntity;
        } catch (AnnotationNotFoundException e) {
            logger.warn(getHandleClass()+" does't annotate @RequestChain, resovler won't resolveLoopable this class's SeqRequest",e);
            throw e;
        }
    }

    protected static Object newChainClassInstance(Class clz){
        try {
            Object parent;
            if(clz.isMemberClass()){
                parent = newChainClassInstance(clz.getEnclosingClass());
            }else{
                return clz.newInstance();
            }
            if(parent!=null){
                return clz.getConstructors()[0].newInstance(parent);
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new ExecutorInitializeException("chain class instance initialize failed",e);
        }
        return null;
    }

    @Override
    public String getDefaultName() {
        return getHandleClass().getSimpleName();
    }
}
package com.dlong.creeper.resolver.base;

import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.exception.ExecutorInitializeException;
import com.dlong.creeper.model.seq.RequestChainEntity;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public class BaseChainAnnoResolver extends LoopableSeqResolver{
    private static Logger logger=Logger.getLogger(BaseChainAnnoResolver.class);

    public BaseChainAnnoResolver(Class<?> handleClass, Class<? extends Annotation> chainAnnoClass) {
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
package com.dlong.creeper.resolver;

import com.dlong.creeper.model.seq.RequestChainEntity;
import com.dlong.creeper.model.seq.SequentialEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

public class ApplicationContextChainResolver extends ChainsMappingResolver implements ApplicationContextAware{
    private ApplicationContext beanContext;

    public ApplicationContextChainResolver() {
    }

    public ApplicationContextChainResolver(boolean fixIndex) {
        super(fixIndex);
    }

    @Override
    public RequestChainEntity resolve(Class chainClass) {
        RequestChainEntity chainEntity = super.resolve(chainClass);
        if(this.beanContext!=null){
            setBeanForChain(chainEntity);
        }
        return chainEntity;
    }

    @Override
    public RequestChainEntity resolve(Class chainClass, RequestChainEntity parent) {
        RequestChainEntity chainEntity = super.resolve(chainClass, parent);
        if(this.beanContext!=null){
            setBeanForChain(chainEntity);
        }
        return chainEntity;
    }

    private void setBeanForChain(RequestChainEntity chainEntity){
        Object beanInstance = this.beanContext.getBean(chainEntity.getChainClass());
        if(beanInstance != null){
            chainEntity.setChainInstance(beanInstance);
        }
        List<SequentialEntity> sequentialList = chainEntity.getSequentialList();
        if(sequentialList.size()>0){
            for (SequentialEntity sequentialEntity : sequentialList) {
                if (sequentialEntity instanceof RequestChainEntity) {
                    setBeanForChain((RequestChainEntity) sequentialEntity);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanContext = applicationContext;
    }
}

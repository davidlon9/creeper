package com.davidlong.http.resolver;

import com.davidlong.http.model.seq.RequestChainEntity;
import com.davidlong.http.model.seq.SequentialEntity;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class ApplicationContextChainResolver extends ChainsMappingResolver{
    private ApplicationContext beanContext;

    public ApplicationContextChainResolver(Class<?> rootClass,ApplicationContext beanContext) {
        super(rootClass);
        this.beanContext = beanContext;
    }

    public ApplicationContextChainResolver(Class<?> rootClass, boolean fixIndex,ApplicationContext beanContext) {
        this(rootClass,beanContext);
        super.setFixIndex(fixIndex);
    }

    @Override
    public RequestChainEntity resolve() {
        RequestChainEntity chainEntity = super.resolve();
        setBeanForChain(chainEntity);
        return chainEntity;
    }

    public void setBeanForChain(RequestChainEntity chainEntity){
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
}

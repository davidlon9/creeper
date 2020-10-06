package com.dlong.creeper.resolver;

import com.dlong.creeper.model.seq.RequestChainEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Iterator;
import java.util.Map;

public class ApplicationContextChainResolver extends ChainsMappingResolver implements ApplicationContextAware{
    private Logger logger=Logger.getLogger(ApplicationContextChainResolver.class);

    private ApplicationContext beanContext;

    public ApplicationContextChainResolver() {
    }

    public ApplicationContextChainResolver(boolean fixIndex) {
        super(fixIndex);
    }

    @Override
    public RequestChainEntity resolve(Class<?> chainClass) {
        return this.resolve(chainClass,null);
    }

    @Override
    public RequestChainEntity resolve(Class<?> chainClass, RequestChainEntity parent) {
        registerBean(chainClass.getSimpleName(),chainClass,null);
        RequestChainEntity chainEntity = super.resolve(chainClass, parent);
        if(this.beanContext!=null){
            setBeanForChain(chainEntity);
        }
        return chainEntity;
    }

    private void setBeanForChain(RequestChainEntity chainEntity){
        Object beanInstance = this.beanContext.getBean(chainEntity.getChainClass().getSimpleName(),chainEntity.getChainClass());
        if(beanInstance != null){
            chainEntity.setChainInstance(beanInstance);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanContext = applicationContext;
    }

    private void registerBean(String beanId, Class<?> beanClass, Map propertyMap) {
        ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) this.beanContext;
        BeanDefinitionRegistry beanDefinitionRegistry = (DefaultListableBeanFactory) configurableContext.getBeanFactory();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        if(propertyMap!=null){
            Iterator<?> entries = propertyMap.entrySet().iterator();
            Map.Entry<?, ?> entry;
            while (entries.hasNext()) {
                entry = (Map.Entry<?, ?>) entries.next();
                String key = (String) entry.getKey();
                Object val = entry.getValue();
                beanDefinitionBuilder.addPropertyValue(key, val);
            }
        }
        BeanDefinition beanDefinition=beanDefinitionBuilder.getBeanDefinition();
        System.out.println("regist bean "+beanDefinition.getBeanClassName());
        beanDefinitionRegistry.registerBeanDefinition(beanId,beanDefinition);
    }

}

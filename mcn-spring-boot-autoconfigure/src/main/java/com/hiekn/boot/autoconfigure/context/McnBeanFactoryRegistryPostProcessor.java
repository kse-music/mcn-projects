package com.hiekn.boot.autoconfigure.context;

import com.hiekn.boot.autoconfigure.base.service.McnAutowired;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigUtils;

/**
 *
 *
 * @author: DingHao
 * @date: 2019/1/7 2:09
 */
public class McnBeanFactoryRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.getBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME).getPropertyValues().add("mcnAutowired", McnAutowired.class);
        beanFactory.addBeanPostProcessor(new McnBeanPostProcessor());
    }

}

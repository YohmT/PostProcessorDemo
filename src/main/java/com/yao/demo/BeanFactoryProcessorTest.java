package com.yao.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @description：
 * @author： Yao
 * @date： 2024/7/11
 */
@Slf4j
@Component
public class BeanFactoryProcessorTest implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            if (name.contains("user")) {
                log.warn("Bean of User: BeanFactoryPostProcessor的postProcessBeanFactory执行");
            }
        }
    }

}

package com.yao.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @description：
 * @author： Yao
 * @date： 2024/7/11
 */
@Slf4j
@Component
public class PostProcessorTest implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.contains("user")) {
            log.warn("Bean of User: BeanPostProcessor中postProcessBeforeInitialization执行");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.contains("user")) {
            log.warn("Bean of User: BeanPostProcessor中postProcessAfterInitialization执行");
        }
        return bean;
    }
}

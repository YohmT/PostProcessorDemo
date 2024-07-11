package com.yao.demo.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description：
 * @author： Yao
 * @date： 2024/7/11
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "user")
public class User implements InitializingBean, DisposableBean {

    private String name;

    private int age;

    public User() {
        log.warn("Bean of User: 实例化");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        log.warn("Bean of User: 设置属性");
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public void destroy() throws Exception {
        log.warn("Bean of User: DisposableBean执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.warn("Bean of User: InitializingBean执行");
    }
}

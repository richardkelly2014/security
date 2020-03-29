package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Created by jiangfei on 2020/3/28.
 */
@Slf4j
@Component
public class DemoBean implements FactoryBean<JwtAuthenticationResponse>,
        BeanNameAware, ApplicationContextAware, InitializingBean, DisposableBean, SmartLifecycle {

    // 1
    @Override
    public void setBeanName(String s) {
        log.info("22133 {}",s);
    }

    @Override
    public void destroy() throws Exception {
        log.info("12333");
    }

    @Override
    public JwtAuthenticationResponse getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    // 3
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("11233");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        // 2
        log.info("{}", applicationContext);
    }

    @Override
    public boolean isAutoStartup() {
        // 4
        log.info("isAutoStartup");
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        log.info("22 {}", runnable);

    }

    @Override
    public void start() {
        log.info("33");

    }

    @Override
    public void stop() {
        log.info("44");

    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}

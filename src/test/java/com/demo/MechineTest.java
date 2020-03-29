package com.demo;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangfei on 2019/10/27.
 */
@Slf4j
public class MechineTest {

    @Test
    public void test1() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class<?>[] EMPTY_CLASSES = {};
        Object[] EMPTY_OBJECTS = {};

        ClassLoader loader = null;
        String value;
        loader = MechineTest.class.getClassLoader();

        // Invoke java.lang.management.ManagementFactory.getRuntimeMXBean().getName()
        Class<?> mgmtFactoryType = Class.forName("java.lang.management.ManagementFactory", true, loader);
        Class<?> runtimeMxBeanType = Class.forName("java.lang.management.RuntimeMXBean", true, loader);

        Method getRuntimeMXBean = mgmtFactoryType.getMethod("getRuntimeMXBean", EMPTY_CLASSES);
        Object bean = getRuntimeMXBean.invoke(null, EMPTY_OBJECTS);
        Method getName = runtimeMxBeanType.getMethod("getName", EMPTY_CLASSES);
        value = (String) getName.invoke(bean, EMPTY_OBJECTS);

        log.info("{}", value);
    }

    @Test
    public void test2() {
        AbstractExecutorService executorService = new AbstractExecutorService() {

            @Override
            public void execute(Runnable command) {

            }

            @Override
            public void shutdown() {

            }

            @Override
            public List<Runnable> shutdownNow() {
                return null;
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return false;
            }
        };
    }

    @Test
    public void test3() {
        String geoHashs = null;
        Iterator<String> iterator = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .split(geoHashs).iterator();
        while (iterator.hasNext()) {
            String geohash = iterator.next();
            log.info("{}", geohash);
        }
    }

}

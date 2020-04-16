package com.base;

import com.resource.ObjectCleaner;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jiangfei on 2019/10/13.
 */
@Slf4j
public class ObjectCleanerTest {

    @Test
    public void test1() throws IOException {

        t();
        System.in.read();

    }

    private void t() {
        A a = create();

        ObjectCleaner.register(a, null);
        String v = a.getName();
        //a = null;
        v = null;
        a.clean();

        //System.gc();
    }


    public A create() {

        return A.builder().name("12121").build();
    }


    @Data
    @Builder
    public static class A {
        private String name;

        public void clean() {
            this.name = null;
        }
    }

    private Thread temporaryThread;
    private Object temporaryObject;

    @Test(timeout = 5000)
    public void testCleanup() throws Exception {
        final AtomicBoolean freeCalled = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        temporaryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException ignore) {
                    // just ignore
                }
            }
        });
        temporaryThread.start();
        ObjectCleaner.register(temporaryThread, new Runnable() {
            @Override
            public void run() {
                freeCalled.set(true);
            }
        });

        latch.countDown();
        temporaryThread.join();
        Assert.assertFalse(freeCalled.get());

        // Null out the temporary object to ensure it is enqueued for GC.
        temporaryThread = null;

        while (!freeCalled.get()) {
            System.gc();
            System.runFinalization();
            Thread.sleep(100);
        }
    }

}

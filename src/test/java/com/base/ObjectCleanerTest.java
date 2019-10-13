package com.base;

import com.resource.ObjectCleaner;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jiangfei on 2019/10/13.
 */
public class ObjectCleanerTest {

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

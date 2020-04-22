package com.disruptor.simple;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LongThreadFactory implements ThreadFactory {

    private AtomicInteger counter;

    public LongThreadFactory() {
        counter = new AtomicInteger(1);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("long-thread-" + counter.getAndIncrement());
        return thread;
    }
}

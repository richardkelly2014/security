package com.disruptor.simple;

import java.util.concurrent.ThreadFactory;

public class LongThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("long-thread");
        return thread;
    }
}

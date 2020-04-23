package com.thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ThreadTest {

    @Test
    public void test1() throws InterruptedException {

        MyThread thread = new MyThread();

        thread.start();

        thread.join();
    }


    class MyThread extends Thread {
        @Override
        public void run() {
            log.info("{}\t{}", Thread.currentThread().getName(), Thread.currentThread().getId());
        }
    }

}

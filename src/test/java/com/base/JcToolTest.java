package com.base;

import com.map.ConcurrentAutoTable;
import com.map.Pow2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.map.UnsafeAccess.UNSAFE;

/**
 * Created by jiangfei on 2019/10/12.
 */
@Slf4j
public class JcToolTest {

    @Test
    public void test1() {

        int CPUs = Runtime.getRuntime().availableProcessors();

        System.out.println(CPUs);

        int value = Integer.numberOfLeadingZeros(CPUs);

        log.info("{},{}", CPUs, value);
    }

    @Test
    public void test2() {

        int value = Pow2.roundToPowerOfTwo(5);
        log.info("{}", value);
    }

    @Test
    public void test3() {
        int base = UNSAFE.arrayBaseOffset(long[].class);
        int scale = UNSAFE.arrayIndexScale(long[].class);

        log.info("{},{}", base, scale);
    }

    @Test
    public void test4() throws InterruptedException {
        ConcurrentAutoTable autoTable = new ConcurrentAutoTable();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    autoTable.increment();
                    log.info("{}", autoTable.get());
                    countDownLatch.countDown();
                }
            });
            thread.setName("t_" + i);
            thread.start();
        }

        countDownLatch.await();
        log.info("{}", 1111);

        autoTable.increment();
        log.info("{}", autoTable.get());

    }

    @Test
    public void test5(){
        AtomicInteger atomicInteger = new AtomicInteger();
        log.info("{}",atomicInteger.getAndIncrement());
    }

}

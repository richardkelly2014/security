package com.disruptor;

import com.Limiter.LeakyBucketLimiter;
import com.Limiter.Limiter;
import com.Limiter.RateLimiter;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LimiterTest {

    @Test
    public void test1() {
        String start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Limiter limiter = new LeakyBucketLimiter(8);

        for (int i = 1; i <= 10; i++) {
            if (limiter.tryAcquire()) {// 请求RateLimiter, 超过permits会被阻塞
                System.out.println("call execute.." + i);
            } else {
                System.out.println("111..." + i);
            }
        }
        String end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("start time:" + start);
        System.out.println("end time:" + end);
    }

    @Test
    public void test2() {

    }

}

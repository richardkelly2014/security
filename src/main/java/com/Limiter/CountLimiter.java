package com.Limiter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CountLimiter extends Limiter {

    private volatile int count;                          // 计数器
    private volatile long lastTime;                      // 时间戳

    public CountLimiter(int qps) {
        super(qps);
        count = 0;
        lastTime = 0;
    }

    @Override
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        if (now - lastTime > 1000) {
            // 保证时间戳后三位都是0
            log.info("{} , {}", lastTime, count);
            lastTime = now >> 3 << 3;
            count = 1;
            return true;
        } else if (count < qps) {
            count++;
            return true;
        } else {
            return false;
        }
    }
}

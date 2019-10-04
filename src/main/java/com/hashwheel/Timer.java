package com.hashwheel;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时器
 */
public interface Timer {

    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);

    Set<Timeout> stop();
}

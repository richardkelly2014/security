package com.hashwheel;

/**
 * 超时
 */
public interface Timeout {

    TimerTask task();

    boolean isExpired();

    boolean isCancelled();

    boolean cancel();
}

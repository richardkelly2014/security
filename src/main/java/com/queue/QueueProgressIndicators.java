package com.queue;

/**
 * 队列处理 游标
 */
public interface QueueProgressIndicators {

    long currentProducerIndex();

    long currentConsumerIndex();
}

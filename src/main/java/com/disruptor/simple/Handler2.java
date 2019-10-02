package com.disruptor.simple;


import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Handler2 implements EventHandler<LongEvent>,WorkHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        
        log.debug("Handler2 消费完成 :{}", event.toString());
    }

    @Override
    public void onEvent(LongEvent event) throws Exception {
        log.debug("work Handler2 消费完成 :{}", event.toString());
    }
}

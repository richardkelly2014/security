package com.disruptor;

import com.disruptor.simple.*;
import com.google.common.base.Stopwatch;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Demo {



//    public static void main(String[] args) throws IOException {
//        Stopwatch stopwatch = Stopwatch.createStarted();
//        Disruptor disruptor = getDisruptor();
//        // 发布事件
//        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
//
//        final LongEventProducerTranslator TRANSLATOR = new LongEventProducerTranslator(ringBuffer);
//
//        //ringBuffer.publishEvent(TRANSLATOR, 100L);
//
//        for (int i = 0; i < 10; i++) {
//            TRANSLATOR.onData(i);
//        }
//
//        disruptor.shutdown();
//        log.debug("任务执行完毕,共耗时{}", stopwatch);
//        System.in.read();
//    }
//
//    public static void main1(String[] args) throws IOException {
//        Stopwatch stopwatch = Stopwatch.createStarted();
//        Disruptor disruptor = getDisruptor();
//        // 发布事件
//        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
//        long sequence = ringBuffer.next();//请求下一个事件序号；
//        try {
//            LongEvent event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
//            event.setValue(123);
//        } finally {
//            ringBuffer.publish(sequence);//发布事件；
//        }
//        disruptor.shutdown();
//        log.debug("任务执行完毕,共耗时{}", stopwatch);
//        System.in.read();
//    }

    private Disruptor getDisruptor() {
        EventFactory<LongEvent> eventFactory = new LongEventFactory();

        int ringBufferSize = 1024 * 1024; // ringBufferSize 大小，必须是 2 的 N 次方；
        Disruptor<LongEvent> disruptor = null;

        disruptor = new Disruptor(eventFactory, ringBufferSize, new LongThreadFactory(),
                ProducerType.SINGLE, new YieldingWaitStrategy());

        disruptor.handleEventsWith(new LongEventHandler());
        //启动
        disruptor.start();
        return disruptor;
    }
}

package com.disruptor;

import com.disruptor.simple.*;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class SimpleDisruptorTest {

    @Test
    public void test1() throws IOException {
        EventFactory<LongEvent> eventFactory = new LongEventFactory();

        int ringBufferSize = 1024 * 1024; // ringBufferSize 大小，必须是 2 的 N 次方；
        Disruptor<LongEvent> disruptor = null;

        disruptor = new Disruptor(eventFactory, ringBufferSize, new LongThreadFactory(),
                ProducerType.SINGLE, new YieldingWaitStrategy());
        // 3个 handler 同时消费
        //disruptor.handleEventsWith(new Handler1(), new Handler2(), new Handler3());

        // 1 2 先消费，在3 消费
//        EventHandlerGroup<LongEvent> group = disruptor.handleEventsWith(new Handler1(), new Handler2());
//        group.then(new Handler3());

//        Handler1 handler1 = new Handler1();
//        Handler2 handler2 = new Handler2();
//        Handler3 handler3 = new Handler3();
//        Handler4 handler4 = new Handler4();
//        Handler5 handler5 = new Handler5();
//
//        // 六边形消费
//        disruptor.handleEventsWith(handler1, handler2);
//
//        disruptor.after(handler1).handleEventsWith(handler4);
//        disruptor.after(handler2).handleEventsWith(handler5);
//
//        disruptor.after(handler4, handler5).handleEventsWith(handler3);

        // 集群消费，只有一个消费
        disruptor.handleEventsWithWorkerPool(new Handler1(), new Handler2());

        disruptor.start();
        // 发布事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();//请求下一个事件序号；
        try {
            LongEvent event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
            event.setValue(123);
        } finally {
            ringBuffer.publish(sequence);//发布事件；
        }
        disruptor.shutdown();

        System.in.read();
    }
}

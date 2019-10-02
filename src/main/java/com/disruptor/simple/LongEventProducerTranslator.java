package com.disruptor.simple;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class LongEventProducerTranslator implements EventTranslatorOneArg<LongEvent, Long> {
    private RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void translateTo(LongEvent event, long sequence, Long arg0) {
        event.setValue(arg0);
    }

    public void onData(long value) {
        ringBuffer.tryPublishEvent(this, value);
    }

}

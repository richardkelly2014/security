package com.queue;

import static com.queue.LinkedArrayQueueUtil.*;

/**
 * Created by jiangfei on 2020/5/2.
 */
public class MpscUnboundedArrayQueue<E> extends BaseMpscLinkedArrayQueue<E> {


    long p0, p1, p2, p3, p4, p5, p6, p7;
    long p10, p11, p12, p13, p14, p15, p16, p17;

    public MpscUnboundedArrayQueue(int chunkSize) {
        super(chunkSize);
    }


    @Override
    protected long availableInQueue(long pIndex, long cIndex) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int capacity() {
        return MessagePassingQueue.UNBOUNDED_CAPACITY;
    }

    @Override
    public int drain(Consumer<E> c) {
        return drain(c, 4096);
    }

    @Override
    public int fill(Supplier<E> s) {
        return MessagePassingQueueUtil.fillUnbounded(this, s);
    }

    @Override
    protected int getNextBufferSize(E[] buffer) {
        return length(buffer);
    }

    @Override
    protected long getCurrentBufferCapacity(long mask) {
        return mask;
    }
}
